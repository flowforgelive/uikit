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
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.components.blocks.panel.PanelSide
import com.uikit.components.blocks.panel.PanelVariant
import com.uikit.foundation.ComponentSize
import com.uikit.foundation.LayoutDirection
import com.uikit.foundation.ThemeMode
import com.uikit.tokens.DesignTokens
import com.uikit.tokens.scaled
import catalog.CatalogOptions

@Composable
internal fun PrimitivesScreen(
	tokens: DesignTokens,
	currentMode: ThemeMode,
	currentDir: LayoutDirection,
	currentBg: BackgroundMode = BackgroundMode.Dots,
	onDirChange: (LayoutDirection) -> Unit,
	onThemeChange: (String) -> Unit,
	onBgChange: (BackgroundMode) -> Unit = {},
	onBack: () -> Unit,
) {
	var globalSizeId by remember { mutableStateOf(ComponentSize.Md.name) }
	var globalRadiusId by remember { mutableStateOf("md") }
	var panelVariant by remember { mutableStateOf(PanelVariant.Inset) }
	var panelSide by remember { mutableStateOf(PanelSide.Left) }
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

	CatalogPage(
		title = "Примитивы (Primitives)",
		subtitle = "Text, Icon, Divider, Image, Skeleton, Spacer, Badge, Surface",
		tokens = modifiedTokens,
		onBack = onBack,
		panelVariant = panelVariant,
		panelSide = panelSide,
		panelTokens = modifiedTokens,
		panelContent = {
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				TextBlock(text = "Направление", variant = TextBlockVariant.LabelSmall)
				DirSwitcherControl(currentDir, onDirChange)
			}
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				TextBlock(text = "Размер", variant = TextBlockVariant.LabelSmall)
				SegmentedControl(
					options = SIZE_OPTIONS,
					selectedId = globalSizeId,
					onSelectionChange = { globalSizeId = it },
					size = ComponentSize.Sm,
				)
			}
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				TextBlock(text = "Скругление", variant = TextBlockVariant.LabelSmall)
				SegmentedControl(
					options = RADIUS_OPTIONS,
					selectedId = globalRadiusId,
					onSelectionChange = { globalRadiusId = it },
					size = ComponentSize.Sm,
				)
			}
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				TextBlock(text = "Тема", variant = TextBlockVariant.LabelSmall)
				ThemeSwitcherControl(currentMode, onThemeChange)
			}
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				TextBlock(text = "Панель: вариант", variant = TextBlockVariant.LabelSmall)
				SegmentedControl(
					options = CatalogOptions.panelVariantOptions.map { it.id to it.label },
					selectedId = if (panelVariant == PanelVariant.Pinned) "pinned" else "inset",
					onSelectionChange = { panelVariant = if (it == "pinned") PanelVariant.Pinned else PanelVariant.Inset },
					size = ComponentSize.Sm,
				)
			}
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				TextBlock(text = "Панель: сторона", variant = TextBlockVariant.LabelSmall)
				SegmentedControl(
					options = CatalogOptions.panelSideOptions.map { it.id to it.label },
					selectedId = panelSide.name.lowercase(),
					onSelectionChange = { id -> panelSide = PanelSide.entries.first { it.name.lowercase() == id } },
					size = ComponentSize.Sm,
				)
			}
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				TextBlock(text = "Фон", variant = TextBlockVariant.LabelSmall)
				BgSwitcherControl(currentBg, onBgChange)
			}
		},
	) {
		CompositionLocalProvider(LocalDesignTokens provides modifiedTokens) {
			TextShowcase(modifiedTokens)
			IconShowcase(modifiedTokens, globalSize)
			DividerShowcase(modifiedTokens)
			ImageShowcase(modifiedTokens, globalSize)
			SkeletonShowcase(modifiedTokens, globalSize)
			SpacerShowcase(modifiedTokens)
			BadgeShowcase(modifiedTokens)
			SurfaceShowcase(modifiedTokens)
		}
	}
}
