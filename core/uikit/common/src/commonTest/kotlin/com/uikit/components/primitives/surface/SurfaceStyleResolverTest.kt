package com.uikit.components.primitives.surface

import com.uikit.foundation.ColorConstants
import com.uikit.foundation.SurfaceLevelResolver
import com.uikit.foundation.VisualVariant
import com.uikit.tokens.DesignTokens
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SurfaceStyleResolverTest {

	private val tokens = DesignTokens.DefaultLight

	// ── Non-interactive: bgHover == bgActive == bg ──

	@Test
	fun nonClickableNonHoverableHasStaticBg() {
		val style = SurfaceStyleResolver.resolve(
			SurfaceConfig(clickable = false, hoverable = false), tokens,
		)
		assertEquals(style.bg, style.bgHover, "non-interactive bgHover must equal bg")
		assertEquals(style.bg, style.bgActive, "non-interactive bgActive must equal bg")
	}

	// ── Clickable: bgHover and bgActive differ ──

	@Test
	fun clickableSurfaceHasDistinctHoverAndActive() {
		val style = SurfaceStyleResolver.resolve(
			SurfaceConfig(clickable = true), tokens,
		)
		assertNotEquals(style.bg, style.bgHover, "clickable bgHover must differ from bg")
		assertNotEquals(style.bg, style.bgActive, "clickable bgActive must differ from bg")
	}

	@Test
	fun hoverableAlsoTriggersInteractiveColors() {
		val style = SurfaceStyleResolver.resolve(
			SurfaceConfig(hoverable = true), tokens,
		)
		assertNotEquals(style.bg, style.bgHover, "hoverable bgHover must differ from bg")
	}

	// ── Border per variant ──

	@Test
	fun outlineVariantUsesBorderFromOutlineVariantToken() {
		val style = SurfaceStyleResolver.resolve(
			SurfaceConfig(variant = VisualVariant.Outline), tokens,
		)
		assertEquals(tokens.color.outlineVariant, style.border)
	}

	@Test
	fun surfaceVariantUsesBorderSubtle() {
		val style = SurfaceStyleResolver.resolve(
			SurfaceConfig(variant = VisualVariant.Surface), tokens,
		)
		assertEquals(tokens.color.borderSubtle, style.border)
	}

	@Test
	fun solidVariantHasTransparentBorder() {
		val style = SurfaceStyleResolver.resolve(
			SurfaceConfig(variant = VisualVariant.Solid), tokens,
		)
		assertEquals(ColorConstants.TRANSPARENT, style.border)
	}

	@Test
	fun softVariantHasTransparentBorder() {
		val style = SurfaceStyleResolver.resolve(
			SurfaceConfig(variant = VisualVariant.Soft), tokens,
		)
		assertEquals(ColorConstants.TRANSPARENT, style.border)
	}

	@Test
	fun ghostVariantHasTransparentBorder() {
		val style = SurfaceStyleResolver.resolve(
			SurfaceConfig(variant = VisualVariant.Ghost), tokens,
		)
		assertEquals(ColorConstants.TRANSPARENT, style.border)
	}

	// ── Bg per variant ──

	@Test
	fun ghostBgIsTransparent() {
		val style = SurfaceStyleResolver.resolve(
			SurfaceConfig(variant = VisualVariant.Ghost), tokens,
		)
		assertEquals(ColorConstants.TRANSPARENT, style.bg)
	}

	@Test
	fun outlineBgIsTransparent() {
		val style = SurfaceStyleResolver.resolve(
			SurfaceConfig(variant = VisualVariant.Outline), tokens,
		)
		assertEquals(ColorConstants.TRANSPARENT, style.bg)
	}

	@Test
	fun softBgUsesSoftColor() {
		SurfaceLevel.entries.forEach { level ->
			val style = SurfaceStyleResolver.resolve(
				SurfaceConfig(variant = VisualVariant.Soft, level = level), tokens,
			)
			assertEquals(
				SurfaceLevelResolver.resolveSoftColor(level, tokens),
				style.bg,
				"Soft bg at $level",
			)
		}
	}

	@Test
	fun solidBgUsesLevelColor() {
		SurfaceLevel.entries.forEach { level ->
			val style = SurfaceStyleResolver.resolve(
				SurfaceConfig(variant = VisualVariant.Solid, level = level), tokens,
			)
			assertEquals(
				SurfaceLevelResolver.resolveColor(level, tokens),
				style.bg,
				"Solid bg at $level",
			)
		}
	}

	// ── Shadow / elevation ──

	@Test
	fun elevatedSurfaceHasShadow() {
		val style = SurfaceStyleResolver.resolve(
			SurfaceConfig(elevated = true), tokens,
		)
		assertEquals(tokens.shadows.md, style.shadow)
		assertTrue(style.elevationDp > 0.0, "elevationDp must be positive")
	}

	@Test
	fun nonElevatedSurfaceHasNoShadow() {
		val style = SurfaceStyleResolver.resolve(
			SurfaceConfig(elevated = false), tokens,
		)
		assertEquals(ColorConstants.SHADOW_NONE, style.shadow)
		assertEquals(0.0, style.elevationDp)
	}

	// ── Shape / radius ──

	@Test
	fun shapeNoneHasZeroRadius() {
		val style = SurfaceStyleResolver.resolve(
			SurfaceConfig(shape = SurfaceShape.None), tokens,
		)
		assertEquals(0.0, style.radius)
	}

	@Test
	fun shapeMdUsesTokenRadius() {
		val style = SurfaceStyleResolver.resolve(
			SurfaceConfig(shape = SurfaceShape.Md), tokens,
		)
		assertEquals(tokens.radius.md, style.radius)
	}

	// ── Border is static (not interactive) for Surface ──

	@Test
	fun surfaceBorderDoesNotChangeOnInteraction() {
		val staticStyle = SurfaceStyleResolver.resolve(
			SurfaceConfig(variant = VisualVariant.Surface, clickable = false), tokens,
		)
		val interactiveStyle = SurfaceStyleResolver.resolve(
			SurfaceConfig(variant = VisualVariant.Surface, clickable = true), tokens,
		)
		assertEquals(staticStyle.border, interactiveStyle.border,
			"Surface border must be identical regardless of clickable")
	}
}
