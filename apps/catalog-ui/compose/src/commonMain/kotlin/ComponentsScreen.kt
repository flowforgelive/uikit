import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.text.TextStyle
import com.uikit.compose.components.composites.segmentedcontrol.SegmentedControl
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.parseColor
import com.uikit.components.blocks.panel.PanelSide
import com.uikit.components.blocks.panel.PanelVariant
import com.uikit.foundation.ComponentSize
import com.uikit.foundation.LayoutDirection
import com.uikit.foundation.ThemeMode
import com.uikit.tokens.DesignTokens

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

	val modifiedTokens = remember(tokens, globalRadiusId) {
		val fraction = RADIUS_FRACTION_MAP[globalRadiusId] ?: 0.2
		if (fraction == tokens.controls.proportions.radiusFraction) tokens
		else tokens.copy(
			controls = tokens.controls.copy(
				proportions = tokens.controls.proportions.copy(
					radiusFraction = fraction,
				),
			),
		)
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
		tokens = tokens,
		onBack = onBack,
		panelVariant = panelVariant,
		panelSide = panelSide,
		panelTokens = modifiedTokens,
		panelContent = {
			// Direction
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				BasicText(
					text = "Направление",
					style = TextStyle(
						fontSize = tokens.typography.labelSmall.fontSize.sp,
						color = parseColor(tokens.color.textMuted),
					),
				)
				DirSwitcherControl(currentDir, onDirChange)
			}
			// Size
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				BasicText(
					text = "Размер",
					style = TextStyle(
						fontSize = tokens.typography.labelSmall.fontSize.sp,
						color = parseColor(tokens.color.textMuted),
					),
				)
				SegmentedControl(
					options = SIZE_OPTIONS,
					selectedId = globalSizeId,
					onSelectionChange = { globalSizeId = it },
					size = ComponentSize.Sm,
				)
			}
			// Radius
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				BasicText(
					text = "Скругление",
					style = TextStyle(
						fontSize = tokens.typography.labelSmall.fontSize.sp,
						color = parseColor(tokens.color.textMuted),
					),
				)
				SegmentedControl(
					options = RADIUS_OPTIONS,
					selectedId = globalRadiusId,
					onSelectionChange = { globalRadiusId = it },
					size = ComponentSize.Sm,
				)
			}
			// Theme
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				BasicText(
					text = "Тема",
					style = TextStyle(
						fontSize = tokens.typography.labelSmall.fontSize.sp,
						color = parseColor(tokens.color.textMuted),
					),
				)
				ThemeSwitcherControl(currentMode, onThemeChange)
			}
			// Panel variant
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				BasicText(
					text = "Панель: вариант",
					style = TextStyle(
						fontSize = tokens.typography.labelSmall.fontSize.sp,
						color = parseColor(tokens.color.textMuted),
					),
				)
				SegmentedControl(
					options = PANEL_VARIANT_OPTIONS,
					selectedId = panelVariantId,
					onSelectionChange = { panelVariantId = it },
					size = ComponentSize.Sm,
				)
			}
			// Panel side
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				BasicText(
					text = "Панель: сторона",
					style = TextStyle(
						fontSize = tokens.typography.labelSmall.fontSize.sp,
						color = parseColor(tokens.color.textMuted),
					),
				)
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
			TextShowcase(modifiedTokens)
			ButtonShowcase(modifiedTokens, globalSize)
			IconButtonShowcase(modifiedTokens, globalSize)
			SurfaceShowcase(modifiedTokens)
			SegmentedControlShowcase(modifiedTokens, globalSize)
			HeightAlignmentShowcase(modifiedTokens, globalSize)
		}
	}
}
