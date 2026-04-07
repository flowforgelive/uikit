package catalog

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * A labeled option for SegmentedControl or similar selectors.
 * Shared between React and Compose catalog apps.
 */
@JsExport
@Serializable
data class CatalogOption(
	val id: String,
	val label: String,
)

/**
 * Single source of truth for catalog UI options (sizes, radii, directions, themes).
 * Both React and Compose catalog apps consume these constants.
 */
@JsExport
object CatalogOptions {

	val dirOptions: Array<CatalogOption> = arrayOf(
		CatalogOption("ltr", "LTR"),
		CatalogOption("rtl", "RTL"),
	)

	val sizeOptions: Array<CatalogOption> = arrayOf(
		CatalogOption("Xs", "XS"),
		CatalogOption("Sm", "SM"),
		CatalogOption("Md", "MD"),
		CatalogOption("Lg", "LG"),
		CatalogOption("Xl", "XL"),
	)

	val radiusOptions: Array<CatalogOption> = arrayOf(
		CatalogOption("none", "0"),
		CatalogOption("sm", "SM"),
		CatalogOption("md", "MD"),
		CatalogOption("lg", "LG"),
		CatalogOption("xl", "XL"),
		CatalogOption("full", "●"),
	)

	val themeOptions: Array<CatalogOption> = arrayOf(
		CatalogOption("dark", "Тёмная"),
		CatalogOption("light", "Светлая"),
		CatalogOption("system", "Система"),
	)

	val panelVariantOptions: Array<CatalogOption> = arrayOf(
		CatalogOption("pinned", "Pinned"),
		CatalogOption("inset", "Inset"),
	)

	val panelSideOptions: Array<CatalogOption> = arrayOf(
		CatalogOption("left", "Left"),
		CatalogOption("right", "Right"),
		CatalogOption("top", "Top"),
		CatalogOption("bottom", "Bottom"),
	)

	fun radiusFraction(id: String): Double = when (id) {
		"none" -> 0.0
		"sm" -> 0.1
		"md" -> 0.2
		"lg" -> 0.35
		"xl" -> 0.5
		"full" -> 1.0
		else -> 0.2
	}

	fun maxContainerRadius(id: String): Double = when (id) {
		"none" -> 0.0
		"sm" -> 8.0
		"md" -> 16.0
		"lg" -> 24.0
		"xl" -> 32.0
		"full" -> 48.0
		else -> 24.0
	}

	fun scaleFactor(sizeId: String): Double = when (sizeId.lowercase()) {
		"xs" -> 0.733
		"sm" -> 0.867
		"md" -> 1.0
		"lg" -> 1.133
		"xl" -> 1.333
		else -> 1.0
	}
}
