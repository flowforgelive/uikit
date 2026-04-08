import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.uikit.compose.components.primitives.spacer.Spacer
import com.uikit.compose.components.primitives.surface.Surface
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.components.primitives.spacer.SpacerAxis
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.compose.theme.parseColor
import com.uikit.foundation.TextEmphasis
import com.uikit.foundation.VisualVariant
import com.uikit.tokens.DesignTokens

private val SPACING_DEMOS = listOf(
	"4" to 4.0,
	"8" to 8.0,
	"12" to 12.0,
	"16" to 16.0,
	"24" to 24.0,
	"32" to 32.0,
)

private val VERTICAL_SUBSET = listOf("8" to 8.0, "16" to 16.0, "32" to 32.0)

@Composable
internal fun SpacerShowcase(tokens: DesignTokens) {
	val barColor = parseColor(tokens.color.primarySoft)
	val barBorderColor = parseColor(tokens.color.primary)

	ShowcaseSection("Разделитель (Spacer)", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xl.dp)) {

			// Visual ruler
			SubSectionTitle(text = "Размеры (визуально)", tokens = tokens)
			TextBlock(
				text = "Цветные полоски показывают высоту каждого spacer-значения.",
				variant = TextBlockVariant.BodySmall,
				emphasis = TextEmphasis.Muted,
			)
			Row(
				horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
				verticalAlignment = Alignment.Bottom,
			) {
				SPACING_DEMOS.forEach { (label, size) ->
					Column(
						horizontalAlignment = Alignment.CenterHorizontally,
						verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp),
					) {
						Box(
							modifier = Modifier
								.width(40.dp)
								.height(size.dp)
								.clip(RoundedCornerShape(tokens.radius.sm.dp))
								.background(barColor)
								.border(1.dp, barBorderColor, RoundedCornerShape(tokens.radius.sm.dp)),
						)
						TextBlock(
							text = "${label}dp",
							variant = TextBlockVariant.LabelSmall,
							emphasis = TextEmphasis.Muted,
						)
					}
				}
			}

			// Vertical between blocks
			SubSectionTitle(text = "Vertical между блоками", tokens = tokens)
			TextBlock(
				text = "Spacer между Surface-блоками. Цветная линейка слева визуализирует размер.",
				variant = TextBlockVariant.BodySmall,
				emphasis = TextEmphasis.Muted,
			)
			Column {
				VERTICAL_SUBSET.forEach { (label, size) ->
					Surface(variant = VisualVariant.Soft) {
						Box(modifier = Modifier.fillMaxWidth().padding(tokens.spacing.sm.dp)) {
							TextBlock(text = "Block", variant = TextBlockVariant.LabelSmall)
						}
					}
					Row(verticalAlignment = Alignment.CenterVertically) {
						Box(
							modifier = Modifier
								.width(4.dp)
								.height(size.dp)
								.clip(RoundedCornerShape(2.dp))
								.background(barBorderColor),
						)
						Box(modifier = Modifier.width(tokens.spacing.xs.dp))
						TextBlock(
							text = "${label}dp",
							variant = TextBlockVariant.LabelSmall,
							emphasis = TextEmphasis.Muted,
						)
						Box(modifier = Modifier.weight(1f)) {
							Spacer(size = size, axis = SpacerAxis.Vertical)
						}
					}
				}
				Surface(variant = VisualVariant.Soft) {
					Box(modifier = Modifier.fillMaxWidth().padding(tokens.spacing.sm.dp)) {
						TextBlock(text = "Block", variant = TextBlockVariant.LabelSmall)
					}
				}
			}

			// Horizontal fixed
			SubSectionTitle(text = "Horizontal (fixed)", tokens = tokens)
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier.fillMaxWidth(),
			) {
				Surface(variant = VisualVariant.Soft) {
					Box(modifier = Modifier.padding(tokens.spacing.sm.dp)) {
						TextBlock(text = "A", variant = TextBlockVariant.LabelSmall)
					}
				}
				Box {
					Box(
						modifier = Modifier
							.matchParentSize()
							.clip(RoundedCornerShape(2.dp))
							.background(barColor),
					)
					Spacer(size = 24.0, axis = SpacerAxis.Horizontal)
				}
				Surface(variant = VisualVariant.Soft) {
					Box(modifier = Modifier.padding(tokens.spacing.sm.dp)) {
						TextBlock(text = "B", variant = TextBlockVariant.LabelSmall)
					}
				}
				Box {
					Box(
						modifier = Modifier
							.matchParentSize()
							.clip(RoundedCornerShape(2.dp))
							.background(barColor),
					)
					Spacer(size = 48.0, axis = SpacerAxis.Horizontal)
				}
				Surface(variant = VisualVariant.Soft) {
					Box(modifier = Modifier.padding(tokens.spacing.sm.dp)) {
						TextBlock(text = "C", variant = TextBlockVariant.LabelSmall)
					}
				}
			}
			TextBlock(
				text = "A — 24dp — B — 48dp — C",
				variant = TextBlockVariant.LabelSmall,
				emphasis = TextEmphasis.Muted,
			)

			// Flexible
			SubSectionTitle(text = "Flexible (size=0)", tokens = tokens)
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier.fillMaxWidth(),
			) {
				Surface(variant = VisualVariant.Soft) {
					Box(modifier = Modifier.padding(tokens.spacing.sm.dp)) {
						TextBlock(text = "Лево", variant = TextBlockVariant.LabelSmall)
					}
				}
				Spacer(size = 0.0, axis = SpacerAxis.Horizontal)
				Surface(variant = VisualVariant.Soft) {
					Box(modifier = Modifier.padding(tokens.spacing.sm.dp)) {
						TextBlock(text = "Право", variant = TextBlockVariant.LabelSmall)
					}
				}
			}
			TextBlock(
				text = "Элементы раздвинуты в противоположные стороны (push-apart).",
				variant = TextBlockVariant.LabelSmall,
				emphasis = TextEmphasis.Muted,
			)
		}
	}
}
