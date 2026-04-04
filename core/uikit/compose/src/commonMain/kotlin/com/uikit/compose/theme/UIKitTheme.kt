package com.uikit.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import com.uikit.foundation.DefaultSurfaceContext
import com.uikit.foundation.DefaultThemeProvider
import com.uikit.foundation.LayoutDirection
import com.uikit.foundation.SurfaceContext
import com.uikit.foundation.ThemeMode
import com.uikit.foundation.ThemeProvider
import com.uikit.tokens.DesignTokens

val LocalDesignTokens = staticCompositionLocalOf { DesignTokens.Default }
val LocalUIKitLayoutDirection = staticCompositionLocalOf { LayoutDirection.Ltr }
val LocalSurfaceContext = staticCompositionLocalOf { DefaultSurfaceContext }
val LocalFontFamily = staticCompositionLocalOf<androidx.compose.ui.text.font.FontFamily> { androidx.compose.ui.text.font.FontFamily.Default }

private fun LayoutDirection.toCompose(): androidx.compose.ui.unit.LayoutDirection =
	when (this) {
		LayoutDirection.Ltr -> androidx.compose.ui.unit.LayoutDirection.Ltr
		LayoutDirection.Rtl -> androidx.compose.ui.unit.LayoutDirection.Rtl
	}

@Composable
fun UIKitTheme(
	tokens: DesignTokens = DesignTokens.Default,
	layoutDirection: LayoutDirection = LayoutDirection.Ltr,
	fontFamily: androidx.compose.ui.text.font.FontFamily = LocalFontFamily.current,
	content: @Composable () -> Unit,
) {
	CompositionLocalProvider(
		LocalDesignTokens provides tokens,
		LocalUIKitLayoutDirection provides layoutDirection,
		LocalLayoutDirection provides layoutDirection.toCompose(),
		LocalFontFamily provides fontFamily,
	) {
		KeyboardNavigationHandler {
			content()
		}
	}
}

/**
 * UIKitTheme с поддержкой ThemeProvider.
 * Автоматически резолвит System → конкретную тему через isSystemInDarkTheme().
 */
@Composable
fun UIKitTheme(
	themeProvider: ThemeProvider = DefaultThemeProvider,
	layoutDirection: LayoutDirection = LayoutDirection.Ltr,
	fontFamily: androidx.compose.ui.text.font.FontFamily = LocalFontFamily.current,
	content: @Composable () -> Unit,
) {
	val themeMode by themeProvider
		.observeThemeMode()
		.collectAsState(initial = ThemeMode.System)

	val isSystemDark = isSystemInDarkTheme()
	val isDark =
		when (themeMode) {
			ThemeMode.Light -> false
			ThemeMode.Dark -> true
			ThemeMode.System -> isSystemDark
		}

	val tokens = if (isDark) DesignTokens.DefaultDark else DesignTokens.DefaultLight

	CompositionLocalProvider(
		LocalDesignTokens provides tokens,
		LocalUIKitLayoutDirection provides layoutDirection,
		LocalLayoutDirection provides layoutDirection.toCompose(),
		LocalFontFamily provides fontFamily,
	) {
		KeyboardNavigationHandler {
			content()
		}
	}
}
