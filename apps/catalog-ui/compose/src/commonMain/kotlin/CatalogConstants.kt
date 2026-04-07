import catalog.CatalogOptions
import com.uikit.components.composites.button.ButtonSize
import com.uikit.foundation.ComponentSize

internal val SIZE_OPTIONS = CatalogOptions.sizeOptions.map { it.id to it.label }

internal val RADIUS_OPTIONS = CatalogOptions.radiusOptions.map { it.id to it.label }

internal val RADIUS_FRACTION_MAP = CatalogOptions.radiusOptions.associate { it.id to CatalogOptions.radiusFraction(it.id) }

internal val MAX_CONTAINER_RADIUS_MAP = CatalogOptions.radiusOptions.associate { it.id to CatalogOptions.maxContainerRadius(it.id) }

internal fun sizeFromId(id: String): ComponentSize =
	ComponentSize.entries.first { it.name == id }

internal fun buttonSizeFromComponentSize(cs: ComponentSize): ButtonSize =
	ButtonSize.entries.first { it.name == cs.name }

internal val PANEL_VARIANT_OPTIONS = CatalogOptions.panelVariantOptions.map { it.id to it.label }

internal val PANEL_SIDE_OPTIONS = CatalogOptions.panelSideOptions.map { it.id to it.label }
