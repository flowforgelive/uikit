import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.text.TextStyle
import com.uikit.compose.components.atoms.button.Button
import com.uikit.compose.components.atoms.iconbutton.IconButton
import com.uikit.compose.components.atoms.segmentedcontrol.SegmentedControl
import com.uikit.compose.theme.parseColor
import com.uikit.foundation.ColorIntent
import com.uikit.foundation.ComponentSize
import com.uikit.foundation.IconPosition
import com.uikit.foundation.VisualVariant
import com.uikit.tokens.DesignTokens
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
internal fun HeightAlignmentShowcase(tokens: DesignTokens, globalSize: ComponentSize) {
	val selectedSize = globalSize
	val btnSize = buttonSizeFromComponentSize(selectedSize)
	val lineColor = Color(0xFFC0C0C0)

	ShowcaseSection("Проверка выравнивания высот (Height Alignment)", tokens) {
		Column(
			verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
		) {
			BasicText(
				text = "Все интерактивные элементы одного размера имеют одинаковую высоту",
				style = TextStyle(
					fontSize = tokens.typography.bodySmall.fontSize.sp,
					lineHeight = tokens.typography.bodySmall.lineHeight.sp,
					letterSpacing = tokens.typography.bodySmall.letterSpacing.sp,
					color = parseColor(tokens.color.textMuted),
				),
			)
			Box {
				Row(
					horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier.padding(vertical = tokens.spacing.xs.dp),
				) {
					Button(
						text = "Button",
						variant = VisualVariant.Solid,
						intent = ColorIntent.Primary,
						size = btnSize,
						iconPosition = IconPosition.Start,
						iconStart = { Icon(Icons.Filled.Search, contentDescription = null) },
					)
					IconButton(
						icon = { Icon(Icons.Filled.Star, contentDescription = null) },
						variant = VisualVariant.Solid,
						intent = ColorIntent.Primary,
						size = selectedSize,
					)
					SegmentedControl(
						options = listOf("a" to "Abc", "b" to "Def"),
						selectedId = "a",
						onSelectionChange = {},
						size = selectedSize,
					)
				}
				Canvas(modifier = Modifier.matchParentSize()) {
					val dash = PathEffect.dashPathEffect(floatArrayOf(6f, 4f))
					drawLine(
						color = lineColor,
						start = Offset(0f, 0f),
						end = Offset(size.width, 0f),
						pathEffect = dash,
					)
					drawLine(
						color = lineColor,
						start = Offset(0f, size.height),
						end = Offset(size.width, size.height),
						pathEffect = dash,
					)
				}
			}
		}
	}
}
