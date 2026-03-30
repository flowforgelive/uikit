package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class DesignTokens(
	val color: ColorTokens,
	val spacing: SpacingTokens,
	val typography: TypographyTokens,
	val sizing: SizingTokens,
	val radius: RadiusTokens,
	val breakpoints: BreakpointTokens,
	val motion: MotionTokens,
	val scaleFactor: Double,
) {
	companion object {
		private val defaultSpacing =
			SpacingTokens(
				xxs = 2.0,
				xs = 4.0,
				sm = 8.0,
				md = 12.0,
				lg = 16.0,
				xl = 24.0,
				xxl = 32.0,
				xxxl = 48.0,
				xxxxl = 64.0,
			)

		private val defaultTypography =
			TypographyTokens(
				largeTitle = TextStyle(fontSize = 34.0, fontWeight = 700, lineHeight = 41.0, letterSpacing = 0.37),
				title1 = TextStyle(fontSize = 28.0, fontWeight = 700, lineHeight = 34.0, letterSpacing = 0.36),
				title2 = TextStyle(fontSize = 22.0, fontWeight = 700, lineHeight = 28.0, letterSpacing = 0.35),
				title3 = TextStyle(fontSize = 20.0, fontWeight = 600, lineHeight = 25.0, letterSpacing = 0.38),
				headline = TextStyle(fontSize = 17.0, fontWeight = 600, lineHeight = 22.0, letterSpacing = -0.41),
				body = TextStyle(fontSize = 17.0, fontWeight = 400, lineHeight = 22.0, letterSpacing = -0.41),
				callout = TextStyle(fontSize = 16.0, fontWeight = 400, lineHeight = 21.0, letterSpacing = -0.32),
				subhead = TextStyle(fontSize = 15.0, fontWeight = 400, lineHeight = 20.0, letterSpacing = -0.24),
				footnote = TextStyle(fontSize = 13.0, fontWeight = 400, lineHeight = 18.0, letterSpacing = -0.08),
				caption1 = TextStyle(fontSize = 12.0, fontWeight = 400, lineHeight = 16.0, letterSpacing = 0.0),
				caption2 = TextStyle(fontSize = 11.0, fontWeight = 400, lineHeight = 13.0, letterSpacing = 0.07),
			)

		private val defaultSizing =
			SizingTokens(
				buttonXs = 24.0,
				buttonSm = 32.0,
				buttonMd = 40.0,
				buttonLg = 48.0,
				buttonXl = 56.0,
				iconXs = 12.0,
				iconSm = 16.0,
				iconMd = 20.0,
				iconLg = 24.0,
				iconXl = 32.0,
				minTouchTarget = 44.0,
			)

		private val defaultRadius =
			RadiusTokens(
				xs = 2.0,
				sm = 4.0,
				md = 8.0,
				lg = 12.0,
				xl = 16.0,
				full = 9999.0,
			)

		private val defaultBreakpoints =
			BreakpointTokens(
				compact = 0.0,
				medium = 600.0,
				expanded = 840.0,
				large = 1200.0,
				extraLarge = 1600.0,
			)

		private val defaultMotion =
			MotionTokens(
				durationInstant = 100,
				durationFast = 150,
				durationNormal = 200,
				durationSlow = 300,
				durationSlower = 400,
				easingStandard = "cubic-bezier(0.2, 0, 0, 1)",
				easingDecelerate = "cubic-bezier(0, 0, 0, 1)",
				easingAccelerate = "cubic-bezier(0.3, 0, 0.8, 0.15)",
				easingEmphasizedDecelerate = "cubic-bezier(0.05, 0.7, 0.1, 1.0)",
				easingEmphasizedAccelerate = "cubic-bezier(0.3, 0, 0.8, 0.15)",
				easingLinear = "linear",
			)

		val DefaultLight =
			DesignTokens(
				color =
					ColorTokens(
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
				spacing = defaultSpacing,
				typography = defaultTypography,
				sizing = defaultSizing,
				radius = defaultRadius,
				breakpoints = defaultBreakpoints,
				motion = defaultMotion,
				scaleFactor = 1.0,
			)

		val DefaultDark =
			DesignTokens(
				color =
					ColorTokens(
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
				spacing = defaultSpacing,
				typography = defaultTypography,
				sizing = defaultSizing,
				radius = defaultRadius,
				breakpoints = defaultBreakpoints,
				motion = defaultMotion,
				scaleFactor = 1.0,
			)

		val Default = DefaultDark
	}
}
