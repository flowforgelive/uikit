package com.uikit.compose.components.primitives.image

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.uikit.components.primitives.skeleton.SkeletonConfig
import com.uikit.components.primitives.skeleton.SkeletonShape
import com.uikit.compose.components.primitives.skeleton.SkeletonView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image as SkiaImage
import java.net.HttpURLConnection
import java.net.URI
import java.util.Collections

private sealed interface ImageLoadState {
	data object Loading : ImageLoadState
	data class Success(val bitmap: ImageBitmap) : ImageLoadState
	data object Error : ImageLoadState
}

private const val MAX_CACHE_SIZE = 50

private val bitmapCache: MutableMap<String, ImageBitmap> = Collections.synchronizedMap(
	object : LinkedHashMap<String, ImageBitmap>(16, 0.75f, true) {
		override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, ImageBitmap>): Boolean =
			size > MAX_CACHE_SIZE
	},
)

private suspend fun downloadBitmap(url: String): ImageBitmap {
	bitmapCache[url]?.let { return it }
	return withContext(Dispatchers.IO) {
		val connection = URI(url).toURL().openConnection() as HttpURLConnection
		connection.connectTimeout = 10_000
		connection.readTimeout = 10_000
		connection.instanceFollowRedirects = true
		try {
			val bytes = connection.inputStream.use { it.readBytes() }
			val bitmap = SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap()
			bitmapCache[url] = bitmap
			bitmap
		} finally {
			connection.disconnect()
		}
	}
}

@Composable
internal actual fun AsyncImageContent(
	src: String,
	fallback: String?,
	contentDescription: String,
	contentScale: ContentScale,
	modifier: Modifier,
) {
	val state by produceState<ImageLoadState>(
		initialValue = bitmapCache[src]?.let { ImageLoadState.Success(it) } ?: ImageLoadState.Loading,
		key1 = src,
	) {
		if (value is ImageLoadState.Success) return@produceState
		value = try {
			ImageLoadState.Success(downloadBitmap(src))
		} catch (e: Exception) {
			System.err.println("Image load failed for $src: ${e.message}")
			if (!fallback.isNullOrEmpty()) {
				try {
					ImageLoadState.Success(downloadBitmap(fallback))
				} catch (fe: Exception) {
					System.err.println("Image fallback failed for $fallback: ${fe.message}")
					ImageLoadState.Error
				}
			} else {
				ImageLoadState.Error
			}
		}
	}

	Crossfade(
		targetState = state,
		animationSpec = tween(durationMillis = 300),
		label = "image-load",
	) { s ->
		when (s) {
			ImageLoadState.Loading -> {
				SkeletonView(
					config = SkeletonConfig(shape = SkeletonShape.Rectangle),
					modifier = modifier,
				)
			}

			is ImageLoadState.Success -> {
				Image(
					painter = BitmapPainter(s.bitmap),
					contentDescription = contentDescription,
					contentScale = contentScale,
					modifier = modifier,
				)
			}

			ImageLoadState.Error -> {
				SkeletonView(
					config = SkeletonConfig(shape = SkeletonShape.Rectangle, animate = false),
					modifier = modifier,
				)
			}
		}
	}
}
