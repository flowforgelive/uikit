import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.CompositionLocalProvider
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.LocalFontFamily
import com.uikit.compose.theme.LocalHazeState
import com.uikit.compose.theme.UIKitTheme
import com.uikit.compose.theme.parseColor
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import com.uikit.foundation.LayoutDirection
import com.uikit.foundation.InMemoryThemeProvider
import com.uikit.foundation.ThemeMode
import kotlinx.coroutines.launch

@Composable
fun CatalogApp(
	fontFamily: androidx.compose.ui.text.font.FontFamily = LocalFontFamily.current,
) {
	var currentScreen by remember { mutableStateOf("first") }
	val themeProvider = remember { InMemoryThemeProvider(ThemeMode.System) }
	var currentDir by remember { mutableStateOf(LayoutDirection.Ltr) }
	var bgMode by remember { mutableStateOf(BackgroundMode.Dots) }
	val scope = rememberCoroutineScope()

	val onNavigate: (String) -> Unit = { route ->
		currentScreen = route.removePrefix("/")
	}

	val onThemeChange: (String) -> Unit = { id ->
		scope.launch {
			themeProvider.setTheme(
				when (id) {
					"dark" -> ThemeMode.Dark
					"light" -> ThemeMode.Light
					else -> ThemeMode.System
				},
			)
		}
	}

	UIKitTheme(themeProvider = themeProvider, layoutDirection = currentDir, fontFamily = fontFamily) {
		val tokens = LocalDesignTokens.current
		val currentMode by themeProvider
			.observeThemeMode()
			.collectAsState(initial = ThemeMode.System)
		val focusManager = LocalFocusManager.current
		val hazeState = rememberHazeState()

		CompositionLocalProvider(LocalHazeState provides hazeState) {
		Box(
			modifier =
				Modifier
					.fillMaxSize()
					.background(parseColor(tokens.color.surface))
					.pointerInput(Unit) {
						detectTapGestures { focusManager.clearFocus() }
					},
		) {
			// Background layer — hazeSource only here, NOT on the parent Box,
			// so glass panels (hazeEffect) sample pure background without circular feedback
			Box(modifier = Modifier.fillMaxSize().hazeSource(state = hazeState)) {
				when (bgMode) {
					BackgroundMode.Dots -> {
						val density = LocalDensity.current
						val dotColor = parseColor(tokens.color.outlineVariant).copy(alpha = 0.15f)
						val gridSizePx = with(density) { 20.dp.toPx() }
						val dotRadiusPx = with(density) { 1.5.dp.toPx() }
						Canvas(modifier = Modifier.fillMaxSize()) {
							var x = 0f
							while (x < size.width) {
								var y = 0f
								while (y < size.height) {
									drawCircle(
										color = dotColor,
										radius = dotRadiusPx,
										center = Offset(x, y),
									)
									y += gridSizePx
								}
								x += gridSizePx
							}
						}
					}
					BackgroundMode.Image -> {
						var bgImage by remember { mutableStateOf<ImageBitmap?>(null) }
						androidx.compose.runtime.LaunchedEffect(Unit) {
							bgImage = withContext(Dispatchers.IO) {
								try {
									val url = java.net.URI("https://picsum.photos/id/1015/1920/1080").toURL()
									val bytes = url.openStream().use { it.readBytes() }
									org.jetbrains.skia.Image.makeFromEncoded(bytes).toComposeImageBitmap()
								} catch (_: Exception) {
									null
								}
							}
						}
						bgImage?.let { img ->
							Image(
								bitmap = img,
								contentDescription = null,
								modifier = Modifier.fillMaxSize(),
								contentScale = ContentScale.Crop,
							)
						}
					}
					BackgroundMode.Solid -> { /* just the base background */ }
				}
			}

			when (currentScreen) {
				"first" -> FirstScreen(
					tokens = tokens,
					currentDir = currentDir,
					currentMode = currentMode,
					onDirChange = { currentDir = it },
					onThemeChange = onThemeChange,
					onNavigate = onNavigate,
				)

				"foundation" -> FoundationScreen(
					tokens = tokens,
					currentMode = currentMode,
					currentDir = currentDir,
					onDirChange = { currentDir = it },
					onThemeChange = onThemeChange,
					onBack = { onNavigate("/first") },
				)

				"primitives" -> PrimitivesScreen(
					tokens = tokens,
					currentMode = currentMode,
					currentDir = currentDir,
					currentBg = bgMode,
					onDirChange = { currentDir = it },
					onThemeChange = onThemeChange,
					onBgChange = { bgMode = it },
					onBack = { onNavigate("/first") },
				)

				"composites" -> CompositesScreen(
					tokens = tokens,
					currentMode = currentMode,
					currentDir = currentDir,
					currentBg = bgMode,
					onDirChange = { currentDir = it },
					onThemeChange = onThemeChange,
					onBgChange = { bgMode = it },
					onBack = { onNavigate("/first") },
				)
			}
		}
		}
	}
}
