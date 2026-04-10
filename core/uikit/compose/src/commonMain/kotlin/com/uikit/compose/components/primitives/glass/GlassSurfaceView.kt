package com.uikit.compose.components.primitives.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import com.uikit.components.primitives.glass.GlassSurfaceConfig
import com.uikit.components.primitives.glass.GlassSurfaceStyleResolver
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.LocalHazeState
import com.uikit.compose.theme.LocalSurfaceContext
import com.uikit.compose.theme.parseColor
import com.uikit.foundation.SurfaceContext
import com.uikit.foundation.GlassVariant
import com.uikit.foundation.Visibility

/**
 * Config-based GlassSurface composable.
 *
 * Uses Haze library for real backdrop blur when HazeState is provided via LocalHazeState.
 * Falls back to semi-transparent tinted background when Haze is not available.
 */
@Composable
fun GlassSurfaceView(
	config: GlassSurfaceConfig,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	if (config.visibility == Visibility.Gone) return

	val tokens = LocalDesignTokens.current
	val hazeState = LocalHazeState.current
	val parentSurfaceContext = LocalSurfaceContext.current
	val style = remember(config, tokens) { GlassSurfaceStyleResolver.resolve(config, tokens) }
	val shape = RoundedCornerShape(style.radius.dp)
	val isRegular = config.variant == GlassVariant.Regular

	val bgColor = remember(style.bg, style.bgOpacity) {
		parseColor(style.bg).copy(alpha = style.bgOpacity.toFloat())
	}
	val borderColor = remember(style.border, style.borderOpacity) {
		parseColor(style.border).copy(alpha = style.borderOpacity.toFloat())
	}

	val surfaceContext = remember(parentSurfaceContext.level, style.bg, parentSurfaceContext.nestingDepth) {
		SurfaceContext(
			level = parentSurfaceContext.level,
			backgroundColor = style.bg,
			nestingDepth = parentSurfaceContext.nestingDepth,
			foregroundColor = style.foregroundColor,
			foregroundSecondary = style.foregroundSecondary,
			foregroundMuted = style.foregroundMuted,
		)
	}

	val hazeModifier = if (hazeState != null) {
		val blurDp = style.blur.dp
		Modifier.hazeEffect(state = hazeState) {
			blurRadius = blurDp
			noiseFactor = 0f
			tints = listOf(HazeTint(bgColor))
		}
	} else {
		Modifier.background(bgColor)
	}

	Box(
		modifier = modifier
			.then(
				if (config.elevated)
					Modifier.shadow(elevation = tokens.shadows.elevationDp.dp, shape = shape)
				else Modifier,
			)
			.clip(shape)
			.then(hazeModifier)
			.border(
				width = tokens.borderWidth.dp,
				color = borderColor,
				shape = shape,
			)
			.then(if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier)
			.testTag(config.testTag ?: config.id),
	) {
		CompositionLocalProvider(LocalSurfaceContext provides surfaceContext) {
			content()
		}
	}
}
