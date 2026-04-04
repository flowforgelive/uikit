import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.text.TextStyle
import catalog.CatalogLayoutResolver
import com.uikit.compose.components.atoms.segmentedcontrol.SegmentedControl
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.parseColor
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
	val globalSize = sizeFromId(globalSizeId)

	val layout = remember(tokens) { CatalogLayoutResolver.resolve(tokens) }

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

	CatalogPage(
		title = "Компоненты (Components)",
		subtitle = "Кнопки, иконки, поверхности, текст, контролы",
		tokens = tokens,
		topBarEnd = {
			Row(
				horizontalArrangement = Arrangement.spacedBy(layout.topBarGap.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				DirSwitcherControl(currentDir, onDirChange)
				BasicText(
					text = "Размер:",
					style = TextStyle(
						fontSize = tokens.typography.labelMedium.fontSize.sp,
						lineHeight = tokens.typography.labelMedium.lineHeight.sp,
						letterSpacing = tokens.typography.labelMedium.letterSpacing.sp,
						color = parseColor(tokens.color.textMuted),
					),
					softWrap = false,
				)
				SegmentedControl(
					options = SIZE_OPTIONS,
					selectedId = globalSizeId,
					onSelectionChange = { globalSizeId = it },
				)
				BasicText(
					text = "Скругление:",
					style = TextStyle(
						fontSize = tokens.typography.labelMedium.fontSize.sp,
						lineHeight = tokens.typography.labelMedium.lineHeight.sp,
						letterSpacing = tokens.typography.labelMedium.letterSpacing.sp,
						color = parseColor(tokens.color.textMuted),
					),
					softWrap = false,
				)
				SegmentedControl(
					options = RADIUS_OPTIONS,
					selectedId = globalRadiusId,
					onSelectionChange = { globalRadiusId = it },
				)
				ThemeSwitcherControl(currentMode, onThemeChange)
			}
		},
		onBack = onBack,
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
