import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.LocalFontFamily
import com.uikit.compose.theme.UIKitTheme
import com.uikit.compose.theme.parseColor
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

		Box(
			modifier =
				Modifier
					.fillMaxSize()
					.background(parseColor(tokens.color.surface))
					.pointerInput(Unit) {
						detectTapGestures { focusManager.clearFocus() }
					},
		) {
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

				"components" -> ComponentsScreen(
					tokens = tokens,
					currentMode = currentMode,
					currentDir = currentDir,
					onDirChange = { currentDir = it },
					onThemeChange = onThemeChange,
					onBack = { onNavigate("/first") },
				)
			}
		}
	}
}
