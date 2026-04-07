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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.uikit.compose.components.composites.button.Button
import com.uikit.compose.components.composites.chip.Chip
import com.uikit.compose.components.composites.segmentedcontrol.SegmentedControl
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.compose.theme.parseColor
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.foundation.ColorIntent
import com.uikit.foundation.ComponentSize
import com.uikit.foundation.TextEmphasis
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
	val lineColor = parseColor(tokens.color.outlineVariant)

	ShowcaseSection("Выравнивание высот (Height Alignment)", tokens) {
		Column(
			verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
		) {
			TextBlock(
				text = "Интерактивные элементы одного размера выровнены по высоте внутри своей группы",
				variant = TextBlockVariant.BodySmall,
				emphasis = TextEmphasis.Muted,
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
						icon = { Icon(Icons.Filled.Search, contentDescription = null) },
					)
					Button(
						icon = { Icon(Icons.Filled.Star, contentDescription = null) },
						variant = VisualVariant.Solid,
						intent = ColorIntent.Primary,
						size = btnSize,
						ariaLabel = "Star",
					)
					SegmentedControl(
						options = listOf("a" to "Abc", "b" to "Def"),
						selectedId = "a",
						onSelectionChange = {},
						size = selectedSize,
					)
					Chip(
						text = "Chip",
						size = selectedSize,
					)
					Chip(
						text = "С иконкой",
						size = selectedSize,
						leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
					)
					Chip(
						text = "Dismiss",
						size = selectedSize,
						dismissible = true,
						onDismiss = {},
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
