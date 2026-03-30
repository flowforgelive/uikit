package com.uikit.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.uikit.foundation.DefaultThemeProvider
import com.uikit.foundation.ThemeMode
import com.uikit.foundation.ThemeProvider
import com.uikit.tokens.DesignTokens

val LocalDesignTokens = staticCompositionLocalOf { DesignTokens.Default }

@Composable
fun UIKitTheme(
	tokens: DesignTokens = DesignTokens.Default,
	content: @Composable () -> Unit,
) {
	CompositionLocalProvider(LocalDesignTokens provides tokens) {
		content()
	}
}

/**
 * UIKitTheme с поддержкой ThemeProvider.
 * Автоматически резолвит System → конкретную тему через isSystemInDarkTheme().
 */
@Composable
fun UIKitTheme(
	themeProvider: ThemeProvider = DefaultThemeProvider,
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

	CompositionLocalProvider(LocalDesignTokens provides tokens) {
		content()
	}
}
