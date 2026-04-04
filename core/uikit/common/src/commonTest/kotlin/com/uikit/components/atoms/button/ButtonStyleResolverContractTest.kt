package com.uikit.components.atoms.button

import com.uikit.foundation.ComponentSize
import com.uikit.foundation.ComponentSizeResolver
import com.uikit.tokens.DesignTokens
import kotlin.test.Test
import kotlin.test.assertEquals

class ButtonStyleResolverContractTest {

	private val tokens = DesignTokens.DefaultDark

	@Test
	fun allControlSizeScaleFieldsCoveredBySizeSet() {
		ComponentSize.entries.forEach { size ->
			val scale = ComponentSizeResolver.resolve(size, tokens.controls, tokens.scaleFactor)
			val config = ButtonConfig(text = "Test", size = size)
			val style = ButtonStyleResolver.resolve(config, tokens, null)
			val sizes = style.sizes

			assertEquals(scale.height, sizes.height, "height mismatch for $size")
			assertEquals(scale.paddingH, sizes.paddingH, "paddingH mismatch for $size")
			assertEquals(scale.fontSize, sizes.fontSize, "fontSize mismatch for $size")
			assertEquals(scale.fontWeight, sizes.fontWeight, "fontWeight mismatch for $size")
			assertEquals(scale.iconSize, sizes.iconSize, "iconSize mismatch for $size")
			assertEquals(scale.iconGap, sizes.iconGap, "iconGap mismatch for $size")
			assertEquals(scale.letterSpacing, sizes.letterSpacing, "letterSpacing mismatch for $size")
			assertEquals(scale.lineHeight, sizes.lineHeight, "lineHeight mismatch for $size")
		}
	}
}
