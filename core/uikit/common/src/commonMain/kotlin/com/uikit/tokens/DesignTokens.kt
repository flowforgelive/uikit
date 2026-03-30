package com.uikit.tokens

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

@JsExport
@Serializable
data class DesignTokens(
    val color: ColorTokens,
    val spacing: SpacingTokens,
    val typography: TypographyTokens,
    val sizing: SizingTokens,
    val radius: RadiusTokens,
) {
    companion object {
        val DefaultLight = DesignTokens(
            color = ColorTokens(
                primary = "#3B82F6",
                primaryHover = "#2563EB",
                secondary = "#6B7280",
                danger = "#EF4444",
                surface = "#FFFFFF",
                surfaceHover = "#F3F4F6",
                textPrimary = "#111827",
                textOnPrimary = "#FFFFFF",
                textOnDanger = "#FFFFFF",
                textDisabled = "#9CA3AF",
                surfaceDisabled = "#E5E7EB",
                borderDisabled = "#D1D5DB",
                border = "#D1D5DB",
            ),
            spacing = SpacingTokens(
                xs = 4,
                sm = 8,
                md = 16,
                lg = 24,
                xl = 32,
            ),
            typography = TypographyTokens(
                h1Size = 32,
                h1Weight = 700,
                h2Size = 24,
                h2Weight = 600,
                h3Size = 20,
                h3Weight = 600,
                bodySize = 16,
                bodyWeight = 400,
                captionSize = 12,
                captionWeight = 400,
            ),
            sizing = SizingTokens(
                buttonSm = 32,
                buttonMd = 40,
                buttonLg = 48,
                iconSm = 16,
                iconMd = 20,
                iconLg = 24,
            ),
            radius = RadiusTokens(
                sm = 4,
                md = 8,
                lg = 12,
            ),
        )

        val DefaultDark = DesignTokens(
            color = ColorTokens(
                primary = "#60A5FA",
                primaryHover = "#93C5FD",
                secondary = "#9CA3AF",
                danger = "#F87171",
                surface = "#111827",
                surfaceHover = "#1F2937",
                textPrimary = "#F9FAFB",
                textOnPrimary = "#111827",
                textOnDanger = "#111827",
                textDisabled = "#4B5563",
                surfaceDisabled = "#1F2937",
                borderDisabled = "#374151",
                border = "#374151",
            ),
            spacing = SpacingTokens(
                xs = 4,
                sm = 8,
                md = 16,
                lg = 24,
                xl = 32,
            ),
            typography = TypographyTokens(
                h1Size = 32,
                h1Weight = 700,
                h2Size = 24,
                h2Weight = 600,
                h3Size = 20,
                h3Weight = 600,
                bodySize = 16,
                bodyWeight = 400,
                captionSize = 12,
                captionWeight = 400,
            ),
            sizing = SizingTokens(
                buttonSm = 32,
                buttonMd = 40,
                buttonLg = 48,
                iconSm = 16,
                iconMd = 20,
                iconLg = 24,
            ),
            radius = RadiusTokens(
                sm = 4,
                md = 8,
                lg = 12,
            ),
        )

        val Default = DefaultDark
    }
}
