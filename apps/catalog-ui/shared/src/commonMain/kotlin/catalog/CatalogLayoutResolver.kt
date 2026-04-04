package catalog

import com.uikit.tokens.DesignTokens
import kotlin.js.JsExport

/**
 * Resolves catalog layout config from DesignTokens.
 * Maps semantic layout slots to specific spacing tokens.
 *
 * This is the SINGLE place where layout decisions are made:
 * "topBar uses md padding" lives HERE, not in React or Compose views.
 */
@JsExport
object CatalogLayoutResolver {

	fun resolve(tokens: DesignTokens): CatalogLayoutConfig = CatalogLayoutConfig(
		contentMaxWidth = 960.0,
		// Top bar
		topBarPaddingBlock = tokens.spacing.md,
		topBarPaddingInline = tokens.spacing.xl,
		topBarGap = tokens.spacing.sm,
		// Content area
		contentPaddingBlockStart = tokens.spacing.xxl,
		contentPaddingBlockEnd = tokens.spacing.xxxxl,
		contentPaddingInline = tokens.spacing.xl,
		// Title area
		titlePaddingBlock = tokens.spacing.lg,
		titleSubtitleGap = tokens.spacing.sm,
		// Sections container
		sectionsGap = tokens.spacing.xxxl,
		// Individual section
		sectionTitleGap = tokens.spacing.lg,
		sectionTitleMarginBottom = tokens.spacing.lg,
		// First (home) screen
		firstScreenGap = tokens.spacing.xl,
		firstScreenControlsPadding = tokens.spacing.lg,
		firstScreenControlsGap = tokens.spacing.sm,
	)
}
