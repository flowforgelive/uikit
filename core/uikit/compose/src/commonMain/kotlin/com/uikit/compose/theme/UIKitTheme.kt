package com.uikit.compose.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
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
