package com.uikit.foundation

import com.uikit.components.primitives.surface.SurfaceLevel
import com.uikit.tokens.DesignTokens
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SurfaceLevelResolverTest {

	private val tokens = DesignTokens.DefaultLight

	// ── resolveColor by enum ──

	@Test
	fun level0ReturnsSurfaceColor() {
		assertEquals(tokens.color.surface, SurfaceLevelResolver.resolveColor(SurfaceLevel.Level0, tokens))
	}

	@Test
	fun level1ReturnsSurfaceContainerLowest() {
		assertEquals(tokens.color.surfaceContainerLowest, SurfaceLevelResolver.resolveColor(SurfaceLevel.Level1, tokens))
	}

	@Test
	fun level2ReturnsSurfaceContainerLow() {
		assertEquals(tokens.color.surfaceContainerLow, SurfaceLevelResolver.resolveColor(SurfaceLevel.Level2, tokens))
	}

	@Test
	fun level3ReturnsSurfaceContainer() {
		assertEquals(tokens.color.surfaceContainer, SurfaceLevelResolver.resolveColor(SurfaceLevel.Level3, tokens))
	}

	@Test
	fun level4ReturnsSurfaceContainerHigh() {
		assertEquals(tokens.color.surfaceContainerHigh, SurfaceLevelResolver.resolveColor(SurfaceLevel.Level4, tokens))
	}

	@Test
	fun level5ReturnsSurfaceContainerHighest() {
		assertEquals(tokens.color.surfaceContainerHighest, SurfaceLevelResolver.resolveColor(SurfaceLevel.Level5, tokens))
	}

	// ── resolveColor by Int ──

	@Test
	fun intOverloadMatchesEnumOverload() {
		SurfaceLevel.entries.forEach { level ->
			assertEquals(
				SurfaceLevelResolver.resolveColor(level, tokens),
				SurfaceLevelResolver.resolveColor(level.ordinal, tokens),
				"int overload must match enum overload for $level",
			)
		}
	}

	// ── resolveHoverColor ──

	@Test
	fun hoverColorIsNextLevel() {
		assertEquals(
			SurfaceLevelResolver.resolveColor(1, tokens),
			SurfaceLevelResolver.resolveHoverColor(0, tokens),
		)
		assertEquals(
			SurfaceLevelResolver.resolveColor(3, tokens),
			SurfaceLevelResolver.resolveHoverColor(2, tokens),
		)
	}

	@Test
	fun hoverColorCapsAtMaxLevel() {
		assertEquals(
			SurfaceLevelResolver.resolveColor(5, tokens),
			SurfaceLevelResolver.resolveHoverColor(5, tokens),
			"hover at level 5 should stay at level 5",
		)
	}

	// ── resolveActiveColor ──

	@Test
	fun activeColorIsTwoLevelsUp() {
		assertEquals(
			SurfaceLevelResolver.resolveColor(2, tokens),
			SurfaceLevelResolver.resolveActiveColor(0, tokens),
		)
		assertEquals(
			SurfaceLevelResolver.resolveColor(4, tokens),
			SurfaceLevelResolver.resolveActiveColor(2, tokens),
		)
	}

	@Test
	fun activeColorFallsBackToSurfaceActiveWhenBeyondMax() {
		assertEquals(
			tokens.color.surfaceActive,
			SurfaceLevelResolver.resolveActiveColor(4, tokens),
			"level 4 + 2 = 6 > 5, should fall back to surfaceActive",
		)
		assertEquals(
			tokens.color.surfaceActive,
			SurfaceLevelResolver.resolveActiveColor(5, tokens),
			"level 5 + 2 = 7 > 5, should fall back to surfaceActive",
		)
	}

	@Test
	fun activeAtLevel3StaysInBounds() {
		assertEquals(
			SurfaceLevelResolver.resolveColor(5, tokens),
			SurfaceLevelResolver.resolveActiveColor(3, tokens),
			"level 3 + 2 = 5, should resolve normally",
		)
	}

	// ── resolveSoftColor ──

	@Test
	fun softLevel0And1ReturnSurface() {
		assertEquals(tokens.color.surface, SurfaceLevelResolver.resolveSoftColor(SurfaceLevel.Level0, tokens))
		assertEquals(tokens.color.surface, SurfaceLevelResolver.resolveSoftColor(SurfaceLevel.Level1, tokens))
	}

	@Test
	fun softLevelsAscendThroughContainerScale() {
		assertEquals(tokens.color.surfaceContainerLowest, SurfaceLevelResolver.resolveSoftColor(SurfaceLevel.Level2, tokens))
		assertEquals(tokens.color.surfaceContainerLow, SurfaceLevelResolver.resolveSoftColor(SurfaceLevel.Level3, tokens))
		assertEquals(tokens.color.surfaceContainer, SurfaceLevelResolver.resolveSoftColor(SurfaceLevel.Level4, tokens))
		assertEquals(tokens.color.surfaceContainerHigh, SurfaceLevelResolver.resolveSoftColor(SurfaceLevel.Level5, tokens))
	}

	// ── Works with Dark tokens too ──

	@Test
	fun darkTokensResolveDifferentColors() {
		val dark = DesignTokens.DefaultDark
		val lightBg = SurfaceLevelResolver.resolveColor(SurfaceLevel.Level0, tokens)
		val darkBg = SurfaceLevelResolver.resolveColor(SurfaceLevel.Level0, dark)
		assertTrue(lightBg != darkBg, "light and dark level 0 should differ")
	}
}
