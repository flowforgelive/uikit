package catalog

import com.uikit.tokens.DesignTokens
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Resolved catalog layout parameters.
 * Single source of truth for layout decisions shared between React and Compose catalog apps.
 * All values are abstract dp (same as DesignTokens).
 */
@JsExport
@Serializable
data class CatalogLayoutConfig(
	val contentMaxWidth: Double,
	// Top bar
	val topBarPaddingBlock: Double,
	val topBarPaddingInline: Double,
	val topBarGap: Double,
	// Content area
	val contentPaddingBlockStart: Double,
	val contentPaddingBlockEnd: Double,
	val contentPaddingInline: Double,
	// Title area
	val titlePaddingBlock: Double,
	val titleSubtitleGap: Double,
	// Sections container
	val sectionsGap: Double,
	// Individual section
	val sectionTitleGap: Double,
	val sectionTitleMarginBottom: Double,
	// First (home) screen
	val firstScreenGap: Double,
	val firstScreenControlsPadding: Double,
	val firstScreenControlsGap: Double,
)
