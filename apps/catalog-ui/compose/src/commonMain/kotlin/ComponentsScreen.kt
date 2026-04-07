import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.uikit.compose.components.composites.segmentedcontrol.SegmentedControl
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.components.blocks.panel.PanelSide
import com.uikit.components.blocks.panel.PanelVariant
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.foundation.ComponentSize
import com.uikit.foundation.LayoutDirection
import com.uikit.foundation.ThemeMode
import com.uikit.tokens.DesignTokens
import com.uikit.tokens.scaled
import catalog.CatalogOptions

@Composable
internal fun ComponentsScreen(
	tokens: DesignTokens,
	currentMode: ThemeMode,
	currentDir: LayoutDirection,
	onDirChange: (LayoutDirection) -> Unit,
	onThemeChange: (String) -> Unit,
	onBack: () -> Unit,
) {
	var globalSizeId by remember { mutableStateOf(ComponentSize.Md.name) }
	var globalRadiusId by remember { mutableStateOf("md") }
	var panelVariantId by remember { mutableStateOf("inset") }
	var panelSideId by remember { mutableStateOf("left") }
	val globalSize = sizeFromId(globalSizeId)

	val modifiedTokens = remember(tokens, globalRadiusId, globalSizeId) {
		var t = tokens
		val fraction = RADIUS_FRACTION_MAP[globalRadiusId] ?: 0.2
		val maxCR = MAX_CONTAINER_RADIUS_MAP[globalRadiusId] ?: 24.0
		if (fraction != t.controls.proportions.radiusFraction ||
			maxCR != t.controls.proportions.maxContainerRadius
		) {
			t = t.copy(
				controls = t.controls.copy(
					proportions = t.controls.proportions.copy(
						radiusFraction = fraction,
						maxContainerRadius = maxCR,
					),
				),
			)
		}
		t.scaled(CatalogOptions.scaleFactor(globalSizeId))
	}

	val panelVariant = when (panelVariantId) {
		"inset" -> PanelVariant.Inset
		else -> PanelVariant.Pinned
	}
	val panelSide = when (panelSideId) {
		"right" -> PanelSide.Right
		"top" -> PanelSide.Top
		"bottom" -> PanelSide.Bottom
		else -> PanelSide.Left
	}

	CatalogPage(
		title = "Компоненты (Components)",
		subtitle = "Кнопки, иконки, поверхности, текст, контролы",
		tokens = modifiedTokens,
		onBack = onBack,
		panelVariant = panelVariant,
		panelSide = panelSide,
		panelTokens = modifiedTokens,
		panelContent = {
			// Direction
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				TextBlock(text = "Направление", variant = TextBlockVariant.LabelSmall)
				DirSwitcherControl(currentDir, onDirChange)
			}
			// Size
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				TextBlock(text = "Размер", variant = TextBlockVariant.LabelSmall)
				SegmentedControl(
					options = SIZE_OPTIONS,
					selectedId = globalSizeId,
					onSelectionChange = { globalSizeId = it },
					size = ComponentSize.Sm,
				)
			}
			// Radius
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				TextBlock(text = "Скругление", variant = TextBlockVariant.LabelSmall)
				SegmentedControl(
					options = RADIUS_OPTIONS,
					selectedId = globalRadiusId,
					onSelectionChange = { globalRadiusId = it },
					size = ComponentSize.Sm,
				)
			}
			// Theme
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				TextBlock(text = "Тема", variant = TextBlockVariant.LabelSmall)
				ThemeSwitcherControl(currentMode, onThemeChange)
			}
			// Panel variant
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				TextBlock(text = "Панель: вариант", variant = TextBlockVariant.LabelSmall)
				SegmentedControl(
					options = PANEL_VARIANT_OPTIONS,
					selectedId = panelVariantId,
					onSelectionChange = { panelVariantId = it },
					size = ComponentSize.Sm,
				)
			}
			// Panel side
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				TextBlock(text = "Панель: сторона", variant = TextBlockVariant.LabelSmall)
				SegmentedControl(
					options = PANEL_SIDE_OPTIONS,
					selectedId = panelSideId,
					onSelectionChange = { panelSideId = it },
					size = ComponentSize.Sm,
				)
			}
		},
	) {
		CompositionLocalProvider(LocalDesignTokens provides modifiedTokens) {
			HeightAlignmentShowcase(modifiedTokens, globalSize)
			// Primitives
			TextShowcase(modifiedTokens)
			IconShowcase(modifiedTokens, globalSize)
			DividerShowcase(modifiedTokens)
			ImageShowcase(modifiedTokens, globalSize)
			SkeletonShowcase(modifiedTokens, globalSize)
			SurfaceShowcase(modifiedTokens)
			// Composites
			ButtonShowcase(modifiedTokens, globalSize)
			IconButtonShowcase(modifiedTokens, globalSize)
			ChipShowcase(modifiedTokens, globalSize)
			SegmentedControlShowcase(modifiedTokens, globalSize)
		}
	}
}
