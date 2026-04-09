package com.uikit.foundation

import com.uikit.tokens.DesignTokens
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class InteractiveColorResolverTest {

	private val tokens = DesignTokens.DefaultLight

	// ── Solid ──

	@Test
	fun solidPrimaryUsesPrimaryBg() {
		val cs = InteractiveColorResolver.resolve(VisualVariant.Solid, ColorIntent.Primary, tokens)
		assertEquals(tokens.color.primary, cs.bg)
		assertEquals(tokens.color.primaryHover, cs.bgHover)
		assertEquals(tokens.color.primaryActive, cs.bgActive)
		assertEquals(tokens.color.textOnPrimary, cs.text)
	}

	@Test
	fun solidNeutralUsesSurfaceContainerHighest() {
		val cs = InteractiveColorResolver.resolve(VisualVariant.Solid, ColorIntent.Neutral, tokens)
		assertEquals(tokens.color.surfaceContainerHighest, cs.bg)
		assertEquals(tokens.color.surfaceHover, cs.bgHover)
		assertEquals(tokens.color.surfaceActive, cs.bgActive)
	}

	@Test
	fun solidDangerUsesDangerBg() {
		val cs = InteractiveColorResolver.resolve(VisualVariant.Solid, ColorIntent.Danger, tokens)
		assertEquals(tokens.color.danger, cs.bg)
		assertEquals(tokens.color.dangerHover, cs.bgHover)
		assertEquals(tokens.color.dangerActive, cs.bgActive)
		assertEquals(tokens.color.textOnDanger, cs.text)
	}

	@Test
	fun solidBordersAreTransparent() {
		ColorIntent.entries.forEach { intent ->
			val cs = InteractiveColorResolver.resolve(VisualVariant.Solid, intent, tokens)
			assertEquals(ColorConstants.TRANSPARENT, cs.border, "border for Solid/$intent")
			assertEquals(ColorConstants.TRANSPARENT, cs.borderHover, "borderHover for Solid/$intent")
			assertEquals(ColorConstants.TRANSPARENT, cs.borderActive, "borderActive for Solid/$intent")
		}
	}

	// ── Soft ──

	@Test
	fun softPrimaryUsesSoftTokens() {
		val cs = InteractiveColorResolver.resolve(VisualVariant.Soft, ColorIntent.Primary, tokens)
		assertEquals(tokens.color.primarySoft, cs.bg)
		assertEquals(tokens.color.primarySoftHover, cs.bgHover)
		assertEquals(tokens.color.primarySoftActive, cs.bgActive)
	}

	@Test
	fun softNeutralUsesSoftTokens() {
		val cs = InteractiveColorResolver.resolve(VisualVariant.Soft, ColorIntent.Neutral, tokens)
		assertEquals(tokens.color.neutralSoft, cs.bg)
		assertEquals(tokens.color.neutralSoftHover, cs.bgHover)
		assertEquals(tokens.color.neutralSoftActive, cs.bgActive)
	}

	@Test
	fun softDangerUsesDangerSoftTokens() {
		val cs = InteractiveColorResolver.resolve(VisualVariant.Soft, ColorIntent.Danger, tokens)
		assertEquals(tokens.color.dangerSoft, cs.bg)
		assertEquals(tokens.color.dangerSoftHover, cs.bgHover)
		assertEquals(tokens.color.dangerSoftActive, cs.bgActive)
	}

	@Test
	fun softBordersAreTransparent() {
		ColorIntent.entries.forEach { intent ->
			val cs = InteractiveColorResolver.resolve(VisualVariant.Soft, intent, tokens)
			assertEquals(ColorConstants.TRANSPARENT, cs.border, "border for Soft/$intent")
		}
	}

	@Test
	fun softNeutralTextEscalatesToPrimaryOnHover() {
		val cs = InteractiveColorResolver.resolve(VisualVariant.Soft, ColorIntent.Neutral, tokens)
		assertEquals(tokens.color.textSecondary, cs.text)
		assertEquals(tokens.color.textPrimary, cs.textHover)
	}

	// ── Surface ──

	@Test
	fun surfacePrimaryHasBordersFromPrimaryBorderTokens() {
		val cs = InteractiveColorResolver.resolve(VisualVariant.Surface, ColorIntent.Primary, tokens)
		assertEquals(tokens.color.primaryBorder, cs.border)
		assertEquals(tokens.color.primaryBorderHover, cs.borderHover)
		assertEquals(tokens.color.primaryBorderActive, cs.borderActive)
	}

	@Test
	fun surfaceNeutralBorderActiveUsesOutlineActive() {
		val cs = InteractiveColorResolver.resolve(VisualVariant.Surface, ColorIntent.Neutral, tokens)
		assertEquals(tokens.color.borderSubtle, cs.border)
		assertEquals(tokens.color.outline, cs.borderHover)
		assertEquals(tokens.color.outlineActive, cs.borderActive)
	}

	@Test
	fun surfaceDangerBordersUseDangerTokens() {
		val cs = InteractiveColorResolver.resolve(VisualVariant.Surface, ColorIntent.Danger, tokens)
		assertEquals(tokens.color.danger, cs.border)
		assertEquals(tokens.color.dangerHover, cs.borderHover)
		assertEquals(tokens.color.dangerActive, cs.borderActive)
	}

	// ── Outline ──

	@Test
	fun outlinePrimaryBgIsTransparent() {
		val cs = InteractiveColorResolver.resolve(VisualVariant.Outline, ColorIntent.Primary, tokens)
		assertEquals(ColorConstants.TRANSPARENT, cs.bg)
		assertEquals(tokens.color.surfaceContainerLowest, cs.bgHover)
		assertEquals(tokens.color.surfaceContainerLow, cs.bgActive)
		assertEquals(tokens.color.primary, cs.text, "Outline/Primary text must be brand color")
		assertEquals(tokens.color.primary, cs.textHover)
		assertEquals(tokens.color.primary, cs.textActive)
	}

	@Test
	fun outlineDangerTextIsStable() {
		val cs = InteractiveColorResolver.resolve(VisualVariant.Outline, ColorIntent.Danger, tokens)
		assertEquals(tokens.color.danger, cs.text, "Outline/Danger text rest")
		assertEquals(tokens.color.danger, cs.textHover, "Outline/Danger text hover must not darken")
		assertEquals(tokens.color.danger, cs.textActive, "Outline/Danger text active must not darken")
	}

	@Test
	fun outlineNeutralBorderActiveUsesOutlineActive() {
		val cs = InteractiveColorResolver.resolve(VisualVariant.Outline, ColorIntent.Neutral, tokens)
		assertEquals(tokens.color.outlineVariant, cs.border)
		assertEquals(tokens.color.outline, cs.borderHover)
		assertEquals(tokens.color.outlineActive, cs.borderActive)
	}

	@Test
	fun outlineDangerBordersUseDangerTokens() {
		val cs = InteractiveColorResolver.resolve(VisualVariant.Outline, ColorIntent.Danger, tokens)
		assertEquals(tokens.color.danger, cs.border)
		assertEquals(tokens.color.dangerHover, cs.borderHover)
		assertEquals(tokens.color.dangerActive, cs.borderActive)
	}

	// ── Ghost ──

	@Test
	fun ghostBgIsTransparentBordersAreTransparent() {
		ColorIntent.entries.forEach { intent ->
			val cs = InteractiveColorResolver.resolve(VisualVariant.Ghost, intent, tokens)
			assertEquals(ColorConstants.TRANSPARENT, cs.bg, "bg for Ghost/$intent")
			assertEquals(ColorConstants.TRANSPARENT, cs.border, "border for Ghost/$intent")
			assertEquals(ColorConstants.TRANSPARENT, cs.borderHover, "borderHover for Ghost/$intent")
			assertEquals(ColorConstants.TRANSPARENT, cs.borderActive, "borderActive for Ghost/$intent")
		}
	}

	@Test
	fun ghostHoverUsesSubtleSurface() {
		val cs = InteractiveColorResolver.resolve(VisualVariant.Ghost, ColorIntent.Primary, tokens)
		assertEquals(tokens.color.surfaceContainerLow, cs.bgHover)
		assertEquals(tokens.color.surfaceContainer, cs.bgActive)
	}

	// ── Disabled ──

	@Test
	fun disabledIsVariantAgnostic() {
		val cs = InteractiveColorResolver.resolveDisabled(tokens)
		assertEquals(tokens.color.surfaceDisabled, cs.bg)
		assertEquals(tokens.color.surfaceDisabled, cs.bgHover)
		assertEquals(tokens.color.surfaceDisabled, cs.bgActive)
		assertEquals(tokens.color.textDisabled, cs.text)
		assertEquals(tokens.color.textDisabled, cs.textHover)
		assertEquals(tokens.color.textDisabled, cs.textActive)
		assertEquals(tokens.color.borderDisabled, cs.border)
		assertEquals(tokens.color.borderDisabled, cs.borderHover)
		assertEquals(tokens.color.borderDisabled, cs.borderActive)
	}

	// ── Border consistency: borderHover ≠ borderActive for Neutral ──

	@Test
	fun neutralBorderHoverDiffersFromBorderActive() {
		listOf(VisualVariant.Surface, VisualVariant.Outline).forEach { variant ->
			val cs = InteractiveColorResolver.resolve(variant, ColorIntent.Neutral, tokens)
			assertNotEquals(cs.borderHover, cs.borderActive,
				"$variant/Neutral must have distinct borderHover vs borderActive")
		}
	}

	// ── All 9 fields populated for every variant×intent ──

	@Test
	fun allFieldsPopulatedForEveryVariantIntentCombination() {
		VisualVariant.entries.forEach { variant ->
			ColorIntent.entries.forEach { intent ->
				val cs = InteractiveColorResolver.resolve(variant, intent, tokens)
				val label = "$variant/$intent"
				assertTrue(cs.bg.isNotBlank(), "$label bg is blank")
				assertTrue(cs.bgHover.isNotBlank(), "$label bgHover is blank")
				assertTrue(cs.bgActive.isNotBlank(), "$label bgActive is blank")
				assertTrue(cs.text.isNotBlank(), "$label text is blank")
				assertTrue(cs.textHover.isNotBlank(), "$label textHover is blank")
				assertTrue(cs.textActive.isNotBlank(), "$label textActive is blank")
				assertTrue(cs.border.isNotBlank(), "$label border is blank")
				assertTrue(cs.borderHover.isNotBlank(), "$label borderHover is blank")
				assertTrue(cs.borderActive.isNotBlank(), "$label borderActive is blank")
			}
		}
	}

	// ── Brand text color contract: transparent variants use brand color ──

	@Test
	fun ghostPrimaryUsesBrandText() {
		val cs = InteractiveColorResolver.resolve(VisualVariant.Ghost, ColorIntent.Primary, tokens)
		assertEquals(tokens.color.primary, cs.text, "Ghost/Primary text must be brand color")
		assertEquals(tokens.color.primary, cs.textHover)
		assertEquals(tokens.color.primary, cs.textActive)
	}

	@Test
	fun transparentVariantsPrimaryUseBrandText() {
		listOf(VisualVariant.Outline, VisualVariant.Ghost).forEach { variant ->
			val cs = InteractiveColorResolver.resolve(variant, ColorIntent.Primary, tokens)
			assertEquals(tokens.color.primary, cs.text, "$variant/Primary text must be brand color")
			assertEquals(tokens.color.primary, cs.textHover, "$variant/Primary textHover must be brand color")
			assertEquals(tokens.color.primary, cs.textActive, "$variant/Primary textActive must be brand color")
		}
	}

	@Test
	fun transparentVariantsDangerUseStableText() {
		listOf(VisualVariant.Outline, VisualVariant.Ghost).forEach { variant ->
			val cs = InteractiveColorResolver.resolve(variant, ColorIntent.Danger, tokens)
			assertEquals(tokens.color.danger, cs.text, "$variant/Danger text")
			assertEquals(tokens.color.danger, cs.textHover, "$variant/Danger textHover must not darken")
			assertEquals(tokens.color.danger, cs.textActive, "$variant/Danger textActive must not darken")
		}
	}
}
