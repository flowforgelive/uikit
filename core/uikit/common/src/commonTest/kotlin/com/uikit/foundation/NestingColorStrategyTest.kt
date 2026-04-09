package com.uikit.foundation

import com.uikit.tokens.DesignTokens
import kotlin.test.Test
import kotlin.test.assertEquals

class NestingColorStrategyTest {

	private val tokens = DesignTokens.DefaultLight

	// ── Primary scale ──

	@Test
	fun primaryDepth0UsesPrimarySoftTokens() {
		val cp = NestingColorStrategy.resolveSoftBg(ColorIntent.Primary, 0, tokens)
		assertEquals(tokens.color.primarySoft, cp.bg)
		assertEquals(tokens.color.primarySoftHover, cp.bgHover)
		assertEquals(tokens.color.primarySoftActive, cp.bgActive)
	}

	@Test
	fun primaryDepth1ShiftsByOne() {
		val cp = NestingColorStrategy.resolveSoftBg(ColorIntent.Primary, 1, tokens)
		assertEquals(tokens.color.primarySoftHover, cp.bg)
		assertEquals(tokens.color.primarySoftActive, cp.bgHover)
		assertEquals(tokens.color.surfaceContainerHigh, cp.bgActive)
	}

	@Test
	fun primaryDepth2ShiftsByTwo() {
		val cp = NestingColorStrategy.resolveSoftBg(ColorIntent.Primary, 2, tokens)
		assertEquals(tokens.color.primarySoftActive, cp.bg)
		assertEquals(tokens.color.surfaceContainerHigh, cp.bgHover)
		assertEquals(tokens.color.surfaceContainerHighest, cp.bgActive)
	}

	@Test
	fun primaryDepth3ShiftsByThree() {
		val cp = NestingColorStrategy.resolveSoftBg(ColorIntent.Primary, 3, tokens)
		assertEquals(tokens.color.surfaceContainerHigh, cp.bg)
		assertEquals(tokens.color.surfaceContainerHighest, cp.bgHover)
		assertEquals(tokens.color.surfaceHover, cp.bgActive)
	}

	// ── Neutral scale ──

	@Test
	fun neutralDepth0UsesNeutralSoftTokens() {
		val cp = NestingColorStrategy.resolveSoftBg(ColorIntent.Neutral, 0, tokens)
		assertEquals(tokens.color.neutralSoft, cp.bg)
		assertEquals(tokens.color.neutralSoftHover, cp.bgHover)
		assertEquals(tokens.color.neutralSoftActive, cp.bgActive)
	}

	@Test
	fun neutralDepth1ShiftsByOne() {
		val cp = NestingColorStrategy.resolveSoftBg(ColorIntent.Neutral, 1, tokens)
		assertEquals(tokens.color.neutralSoftHover, cp.bg)
		assertEquals(tokens.color.neutralSoftActive, cp.bgHover)
		assertEquals(tokens.color.surfaceContainerHigh, cp.bgActive)
	}

	// ── Danger scale ──

	@Test
	fun dangerDepth0UsesDangerSoftTokens() {
		val cp = NestingColorStrategy.resolveSoftBg(ColorIntent.Danger, 0, tokens)
		assertEquals(tokens.color.dangerSoft, cp.bg)
		assertEquals(tokens.color.dangerSoftHover, cp.bgHover)
		assertEquals(tokens.color.dangerSoftActive, cp.bgActive)
	}

	@Test
	fun dangerDepth3UsesContainerTokens() {
		val cp = NestingColorStrategy.resolveSoftBg(ColorIntent.Danger, 3, tokens)
		assertEquals(tokens.color.surfaceContainer, cp.bg)
		assertEquals(tokens.color.surfaceContainerHigh, cp.bgHover)
		assertEquals(tokens.color.surfaceContainerHighest, cp.bgActive)
	}

	// ── Clamping ──

	@Test
	fun negativeDepthClampedToZero() {
		val cp = NestingColorStrategy.resolveSoftBg(ColorIntent.Primary, -5, tokens)
		val cp0 = NestingColorStrategy.resolveSoftBg(ColorIntent.Primary, 0, tokens)
		assertEquals(cp0, cp, "negative depth must clamp to 0")
	}

	@Test
	fun excessiveDepthClampedToMax() {
		val cp = NestingColorStrategy.resolveSoftBg(ColorIntent.Primary, 100, tokens)
		val cpMax = NestingColorStrategy.resolveSoftBg(ColorIntent.Primary, 3, tokens)
		assertEquals(cpMax, cp, "depth beyond max must clamp to max (3 for 6-step scale)")
	}

	// ── Distinct backgrounds at each depth ──

	@Test
	fun eachDepthHasDistinctBgForAllIntents() {
		ColorIntent.entries.forEach { intent ->
			val bgs = (0..3).map { depth ->
				NestingColorStrategy.resolveSoftBg(intent, depth, tokens).bg
			}
			assertEquals(bgs.size, bgs.toSet().size,
				"$intent: each depth must produce a distinct bg, got $bgs")
		}
	}
}
