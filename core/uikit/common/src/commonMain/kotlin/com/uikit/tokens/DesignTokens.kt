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
	val controls: InteractiveControlTokens,
	val shadows: ShadowTokens,
	val state: InteractiveStateTokens,
	val scaleFactor: Double,
	val fontFamilyName: String = "Inter",
	val fontVariationSettings: String = "'opsz' 14",
	val borderWidth: Double = 1.0,
	val focusRingWidth: Double = 2.0,
	val spinnerStrokeWidth: Double = 2.0,
) {
	/**
	 * SSR SAFETY: These values are cached as singletons in Node.js between requests.
	 * Do NOT add mutable state, side effects, or platform-specific API calls here.
	 * All values must be pure data (immutable, deterministic).
	 */
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
				displayLarge = TextStyle(fontSize = 34.0, fontWeight = 700, lineHeight = 41.0, letterSpacing = 0.37),
				displayMedium = TextStyle(fontSize = 28.0, fontWeight = 700, lineHeight = 34.0, letterSpacing = 0.36),
				displaySmall = TextStyle(fontSize = 24.0, fontWeight = 700, lineHeight = 30.0, letterSpacing = 0.35),
				headlineLarge = TextStyle(fontSize = 22.0, fontWeight = 700, lineHeight = 28.0, letterSpacing = 0.35),
				headlineMedium = TextStyle(fontSize = 20.0, fontWeight = 600, lineHeight = 25.0, letterSpacing = 0.38),
				headlineSmall = TextStyle(fontSize = 18.0, fontWeight = 600, lineHeight = 24.0, letterSpacing = -0.20),
				titleLarge = TextStyle(fontSize = 17.0, fontWeight = 600, lineHeight = 22.0, letterSpacing = -0.41),
				titleMedium = TextStyle(fontSize = 16.0, fontWeight = 600, lineHeight = 21.0, letterSpacing = -0.32),
				titleSmall = TextStyle(fontSize = 15.0, fontWeight = 600, lineHeight = 20.0, letterSpacing = -0.24),
				bodyLarge = TextStyle(fontSize = 17.0, fontWeight = 400, lineHeight = 22.0, letterSpacing = -0.41),
				bodyMedium = TextStyle(fontSize = 15.0, fontWeight = 400, lineHeight = 20.0, letterSpacing = -0.24),
				bodySmall = TextStyle(fontSize = 13.0, fontWeight = 400, lineHeight = 18.0, letterSpacing = -0.08),
				labelLarge = TextStyle(fontSize = 13.0, fontWeight = 500, lineHeight = 18.0, letterSpacing = -0.08),
				labelMedium = TextStyle(fontSize = 12.0, fontWeight = 400, lineHeight = 16.0, letterSpacing = 0.0),
				labelSmall = TextStyle(fontSize = 11.0, fontWeight = 400, lineHeight = 13.0, letterSpacing = 0.07),
			)

		private val defaultSizing =
			SizingTokens(
				controlXs = 24.0,
				controlSm = 32.0,
				controlMd = 40.0,
				controlLg = 48.0,
				controlXl = 56.0,
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

		private val defaultControls =
			InteractiveControlTokens(
				proportions = ControlProportions(
					heightRatio = 2.5,
					paddingHRatio = 1.0,
					iconSizeRatio = 1.2,
					iconGapRatio = 0.5,
					radiusFraction = 0.2,
				),
				xs = ControlSizeInput(fontSize = 11.0, fontWeight = 600, letterSpacing = 0.07),
				sm = ControlSizeInput(fontSize = 13.0, fontWeight = 600, letterSpacing = 0.0),
				md = ControlSizeInput(fontSize = 15.0, fontWeight = 600, letterSpacing = -0.24),
				lg = ControlSizeInput(fontSize = 17.0, fontWeight = 600, letterSpacing = -0.41),
				xl = ControlSizeInput(fontSize = 20.0, fontWeight = 600, letterSpacing = -0.41),
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

		private val defaultShadows =
			ShadowTokens(
				sm = "0 1px 2px 0 rgba(0,0,0,0.05)",
				md = "0 1px 3px 0 rgba(0,0,0,0.1), 0 1px 2px -1px rgba(0,0,0,0.1)",
				lg = "0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -2px rgba(0,0,0,0.1)",
				xl = "0 10px 15px -3px rgba(0,0,0,0.1), 0 4px 6px -4px rgba(0,0,0,0.1)",
			)

		private val defaultState =
			InteractiveStateTokens(
				hoverOpacity = 0.08,
				pressOpacity = 0.12,
				disabledOpacity = 0.6,
			)

		val DefaultLight =
			DesignTokens(
				color =
					ColorTokens(
						primary = "#1A1A1A",
						primaryHover = "#333333",
						secondary = "#505050",
						danger = "#DC2626",
						dangerHover = "#B91C1C",
						background = "#F5F5F5",
						surface = "#FFFFFF",
						surfaceContainerLowest = "#FFFFFF",
						surfaceContainerLow = "#F7F7F7",
						surfaceContainer = "#F0F0F0",
						surfaceContainerHigh = "#EAEAEA",
						surfaceContainerHighest = "#E0E0E0",
						surfaceHover = "#D6D6D6",
						onSurface = "#1A1A1A",
						outline = "#787878",
						outlineVariant = "#C4C4C4",
						textPrimary = "#1A1A1A",
						textSecondary = "#505050",
						textMuted = "#888888",
						textOnPrimary = "#FFFFFF",
						textOnDanger = "#FFFFFF",
						textDisabled = "#BBBBBB",
						surfaceDisabled = "#EBEBEB",
						borderDisabled = "#E0E0E0",
						border = "#D6D6D6",
						focusRing = "#787878",
						dangerSoft = "#FEE2E2",
						dangerSoftHover = "#FECACA",
						primarySoft = "#F0F0F0",
						primarySoftHover = "#E5E5E5",
						neutralSoft = "#F5F5F5",
						neutralSoftHover = "#EBEBEB",
						borderSubtle = "#E5E5E5",
						primaryBorder = "#D4D4D4",
						primaryBorderHover = "#A3A3A3",
					),
				spacing = defaultSpacing,
				typography = defaultTypography,
				sizing = defaultSizing,
				radius = defaultRadius,
				breakpoints = defaultBreakpoints,
				motion = defaultMotion,
				controls = defaultControls,
				shadows = defaultShadows,
				state = defaultState,
				scaleFactor = 1.0,
			)

		val DefaultDark =
			DesignTokens(
				color =
					ColorTokens(
						primary = "#FFFFFF",
						primaryHover = "#CCCCCC",
						secondary = "#999999",
						danger = "#F87171",
						dangerHover = "#EF4444",
						background = "#0A0A0C",
						surface = "#0C0C0E",
						surfaceContainerLowest = "#111113",
						surfaceContainerLow = "#171717",
						surfaceContainer = "#1E1E1E",
						surfaceContainerHigh = "#2B2B2B",
						surfaceContainerHighest = "#383838",
						surfaceHover = "#333333",
						onSurface = "#FFFFFF",
						outline = "#8E8E8E",
						outlineVariant = "#444444",
						textPrimary = "#FFFFFF",
						textSecondary = "#999999",
						textMuted = "#666666",
						textOnPrimary = "#000000",
						textOnDanger = "#000000",
						textDisabled = "#444444",
						surfaceDisabled = "#1A1A1A",
						borderDisabled = "#1A1A1A",
						border = "#2A2A2A",
						focusRing = "#8E8E8E",
						dangerSoft = "#3B1111",
						dangerSoftHover = "#4C1414",
						primarySoft = "#1A1A1A",
						primarySoftHover = "#262626",
						neutralSoft = "#1C1C1C",
						neutralSoftHover = "#2A2A2A",
						borderSubtle = "#333333",
						primaryBorder = "#404040",
						primaryBorderHover = "#525252",
					),
				spacing = defaultSpacing,
				typography = defaultTypography,
				sizing = defaultSizing,
				radius = defaultRadius,
				breakpoints = defaultBreakpoints,
				motion = defaultMotion,
				controls = defaultControls,
				shadows = defaultShadows,
				state = defaultState,
				scaleFactor = 1.0,
			)

		val Default = DefaultDark
	}
}
