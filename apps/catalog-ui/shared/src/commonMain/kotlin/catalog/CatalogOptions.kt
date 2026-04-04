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
		CatalogOption("none", "None"),
		CatalogOption("sm", "SM"),
		CatalogOption("md", "MD"),
		CatalogOption("lg", "LG"),
		CatalogOption("xl", "XL"),
		CatalogOption("full", "Full"),
	)

	val themeOptions: Array<CatalogOption> = arrayOf(
		CatalogOption("dark", "Тёмная"),
		CatalogOption("light", "Светлая"),
		CatalogOption("system", "Система"),
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
}
