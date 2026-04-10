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
	val glass: GlassTokens = GlassTokens(),
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
				pressOpacity = 0.12,
			)

		val DefaultLight =
			DesignTokens(
				color =
					ColorTokens(
						primary = "oklch(21.8% 0 0)",
						primaryHover = "oklch(32.1% 0 0)",
						primaryActive = "oklch(38.7% 0 0)",
						secondary = "oklch(43.1% 0 0)",
						danger = "oklch(57.7% 0.2152 27.3)",
						dangerHover = "oklch(50.5% 0.1905 27.5)",
						dangerActive = "oklch(44.4% 0.1613 26.9)",
						background = "oklch(97.0% 0 0)",
						surface = "oklch(100.0% 0 0)",
						surfaceContainerLowest = "oklch(100.0% 0 0)",
						surfaceContainerLow = "oklch(97.6% 0 0)",
						surfaceContainer = "oklch(95.5% 0 0)",
						surfaceContainerHigh = "oklch(93.7% 0 0)",
						surfaceContainerHighest = "oklch(90.7% 0 0)",
						surfaceHover = "oklch(87.6% 0 0)",
						surfaceActive = "oklch(84.5% 0 0)",
						onSurface = "oklch(21.8% 0 0)",
						onSurfaceVariant = "oklch(43.1% 0 0)",
						onSurfaceMuted = "oklch(62.7% 0 0)",
						outline = "oklch(57.3% 0 0)",
						outlineVariant = "oklch(82.0% 0 0)",
						outlineActive = "oklch(47.5% 0 0)",
						textPrimary = "oklch(21.8% 0 0)",
						textSecondary = "oklch(43.1% 0 0)",
						textMuted = "oklch(62.7% 0 0)",
						textOnPrimary = "oklch(100.0% 0 0)",
						textOnDanger = "oklch(100.0% 0 0)",
						textDisabled = "oklch(79.2% 0 0)",
						surfaceDisabled = "oklch(94.0% 0 0)",
						borderDisabled = "oklch(90.7% 0 0)",
						border = "oklch(87.6% 0 0)",
						focusRing = "oklch(57.3% 0 0)",
						dangerSoft = "oklch(93.6% 0.0309 17.7)",
						dangerSoftHover = "oklch(88.5% 0.0593 18.3)",
						dangerSoftActive = "oklch(80.8% 0.1035 19.6)",
						primarySoft = "oklch(95.5% 0 0)",
						primarySoftHover = "oklch(92.2% 0 0)",
						primarySoftActive = "oklch(88.5% 0 0)",
						neutralSoft = "oklch(97.0% 0 0)",
						neutralSoftHover = "oklch(94.0% 0 0)",
						neutralSoftActive = "oklch(90.7% 0 0)",
						borderSubtle = "oklch(92.2% 0 0)",
						primaryBorder = "oklch(87.0% 0 0)",
						primaryBorderHover = "oklch(71.5% 0 0)",
						primaryBorderActive = "oklch(63.3% 0 0)",
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
						primary = "oklch(100.0% 0 0)",
						primaryHover = "oklch(84.5% 0 0)",
						primaryActive = "oklch(76.8% 0 0)",
						secondary = "oklch(65.5% 0 0)",
						danger = "oklch(71.1% 0.1661 22.2)",
						dangerHover = "oklch(63.7% 0.2084 25.8)",
						dangerActive = "oklch(57.7% 0.2152 27.3)",
						background = "oklch(14.6% 0.0043 285.9)",
						surface = "oklch(15.6% 0.0042 285.8)",
						surfaceContainerLowest = "oklch(18.2% 0.0039 285.7)",
						surfaceContainerLow = "oklch(21.6% 0 0)",
						surfaceContainer = "oklch(25.1% 0 0)",
						surfaceContainerHigh = "oklch(30.2% 0 0)",
						surfaceContainerHighest = "oklch(35.1% 0 0)",
						surfaceHover = "oklch(32.1% 0 0)",
						surfaceActive = "oklch(35.4% 0 0)",
						onSurface = "oklch(100.0% 0 0)",
						onSurfaceVariant = "oklch(65.5% 0 0)",
						onSurfaceMuted = "oklch(48.4% 0 0)",
						outline = "oklch(63.8% 0 0)",
						outlineVariant = "oklch(35.4% 0 0)",
						outlineActive = "oklch(73.2% 0 0)",
						textPrimary = "oklch(100.0% 0 0)",
						textSecondary = "oklch(65.5% 0 0)",
						textMuted = "oklch(48.4% 0 0)",
						textOnPrimary = "oklch(0.0% 0 0)",
						textOnDanger = "oklch(0.0% 0 0)",
						textDisabled = "oklch(35.4% 0 0)",
						surfaceDisabled = "oklch(21.8% 0 0)",
						borderDisabled = "oklch(21.8% 0 0)",
						border = "oklch(28.7% 0 0)",
						focusRing = "oklch(63.8% 0 0)",
						dangerSoft = "oklch(18.7% 0.054 20.9)",
						dangerSoftHover = "oklch(22.5% 0.064 19.9)",
						dangerSoftActive = "oklch(25.5% 0.0748 19.5)",
						primarySoft = "oklch(21.8% 0 0)",
						primarySoftHover = "oklch(27.0% 0 0)",
						primarySoftActive = "oklch(30.6% 0 0)",
						neutralSoft = "oklch(23.5% 0 0)",
						neutralSoftHover = "oklch(28.7% 0 0)",
						neutralSoftActive = "oklch(32.1% 0 0)",
						borderSubtle = "oklch(32.1% 0 0)",
						primaryBorder = "oklch(35.4% 0 0)",
						primaryBorderHover = "oklch(40.2% 0 0)",
						primaryBorderActive = "oklch(46.2% 0 0)",
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

fun DesignTokens.scaled(factor: Double): DesignTokens =
	if (factor == 1.0) this
	else copy(
		spacing = spacing.scaled(factor),
		typography = typography.scaled(factor),
		sizing = sizing.scaled(factor),
		controls = controls.scaled(factor),
		borderWidth = borderWidth * factor,
		focusRingWidth = focusRingWidth * factor,
		spinnerStrokeWidth = spinnerStrokeWidth * factor,
	)

@JsExport
fun scaleDesignTokens(tokens: DesignTokens, factor: Double): DesignTokens = tokens.scaled(factor)

@JsExport
fun withControlProportions(tokens: DesignTokens, radiusFraction: Double, maxContainerRadius: Double): DesignTokens =
	tokens.copy(
		controls = tokens.controls.copy(
			proportions = tokens.controls.proportions.copy(
				radiusFraction = radiusFraction,
				maxContainerRadius = maxContainerRadius,
			),
		),
	)
