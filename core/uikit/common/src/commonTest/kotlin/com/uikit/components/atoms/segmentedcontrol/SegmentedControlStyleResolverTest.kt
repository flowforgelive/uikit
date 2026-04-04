package com.uikit.components.atoms.segmentedcontrol

import com.uikit.foundation.ComponentSize
import com.uikit.foundation.ComponentSizeResolver
import com.uikit.tokens.DesignTokens
import kotlin.test.Test
import kotlin.test.assertEquals

class SegmentedControlStyleResolverTest {

	private val tokens = DesignTokens.DefaultDark

	private fun configForSize(size: ComponentSize) = SegmentedControlConfig(
		options = arrayOf(SegmentedControlOption("1", "A"), SegmentedControlOption("2", "B")),
		selectedId = "1",
		size = size,
	)

	@Test
	fun resolvePropagatesFontWeightFromControlSizeScale() {
		ComponentSize.entries.forEach { size ->
			val scale = ComponentSizeResolver.resolve(size, tokens.controls, tokens.scaleFactor)
			val style = SegmentedControlStyleResolver.resolve(configForSize(size), tokens)
			assertEquals(
				scale.fontWeight,
				style.sizes.fontWeight,
				"fontWeight mismatch for size $size: ControlSizeScale=${scale.fontWeight}, SegmentedControlSizes=${style.sizes.fontWeight}",
			)
		}
	}

	@Test
	fun resolvePropagatesLetterSpacingFromControlSizeScale() {
		ComponentSize.entries.forEach { size ->
			val scale = ComponentSizeResolver.resolve(size, tokens.controls, tokens.scaleFactor)
			val style = SegmentedControlStyleResolver.resolve(configForSize(size), tokens)
			assertEquals(
				scale.letterSpacing,
				style.sizes.letterSpacing,
				"letterSpacing mismatch for size $size: ControlSizeScale=${scale.letterSpacing}, SegmentedControlSizes=${style.sizes.letterSpacing}",
			)
		}
	}

	@Test
	fun resolvePropagatesPaddingHFromControlSizeScale() {
		ComponentSize.entries.forEach { size ->
			val scale = ComponentSizeResolver.resolve(size, tokens.controls, tokens.scaleFactor)
			val style = SegmentedControlStyleResolver.resolve(configForSize(size), tokens)
			assertEquals(
				scale.paddingH,
				style.sizes.paddingH,
				"paddingH mismatch for size $size",
			)
		}
	}

	@Test
	fun trackPaddingComesFromTokens() {
		val customTrackPadding = 3.5
		val customTokens = tokens.copy(
			controls = tokens.controls.copy(segmentedControlTrackPadding = customTrackPadding),
		)
		val style = SegmentedControlStyleResolver.resolve(
			configForSize(ComponentSize.Md),
			customTokens,
		)
		assertEquals(customTrackPadding, style.sizes.trackPadding, "trackPadding must come from tokens")
	}

	@Test
	fun thumbRadiusIsRadiusMinusTrackPadding() {
		ComponentSize.entries.forEach { size ->
			val style = SegmentedControlStyleResolver.resolve(configForSize(size), tokens)
			assertEquals(
				style.sizes.radius - style.sizes.trackPadding,
				style.sizes.thumbRadius,
				"thumbRadius must equal radius - trackPadding for size $size",
			)
		}
	}

	@Test
	fun allControlSizeScaleFieldsCoveredBySegmentedControlSizes() {
		val scale = ComponentSizeResolver.resolve(ComponentSize.Md, tokens.controls, tokens.scaleFactor)
		val style = SegmentedControlStyleResolver.resolve(configForSize(ComponentSize.Md), tokens)
		val sizes = style.sizes

		assertEquals(scale.height, sizes.height, "height")
		assertEquals(scale.paddingH, sizes.paddingH, "paddingH")
		assertEquals(scale.fontSize, sizes.fontSize, "fontSize")
		assertEquals(scale.fontWeight, sizes.fontWeight, "fontWeight")
		assertEquals(scale.letterSpacing, sizes.letterSpacing, "letterSpacing")
		assertEquals(scale.radius, sizes.radius, "radius")
		assertEquals(scale.iconSize, sizes.iconSize, "iconSize")
		assertEquals(scale.iconGap, sizes.iconGap, "iconGap")
		assertEquals(scale.lineHeight, sizes.lineHeight, "lineHeight")
	}

	@Test
	fun resolveDefaultUsesSizeSmAndVariantSurface() {
		val defaultStyle = SegmentedControlStyleResolver.resolve(tokens)
		val explicitStyle = SegmentedControlStyleResolver.resolve(
			SegmentedControlConfig(
				options = arrayOf(SegmentedControlOption("1", "A")),
				selectedId = "1",
				size = ComponentSize.Sm,
				variant = com.uikit.foundation.VisualVariant.Surface,
			),
			tokens,
		)
		assertEquals(explicitStyle.sizes, defaultStyle.sizes, "resolveDefault sizes must match Sm")
		assertEquals(explicitStyle.colors, defaultStyle.colors, "resolveDefault colors must match Surface variant")
	}
}
