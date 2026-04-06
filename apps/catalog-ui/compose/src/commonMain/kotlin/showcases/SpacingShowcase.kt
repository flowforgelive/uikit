import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.compose.theme.parseColor
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.tokens.DesignTokens

private data class SpacingEntry(val label: String, val value: Double)

@Composable
internal fun SpacingShowcase(tokens: DesignTokens) {
	val entries =
		listOf(
			SpacingEntry("xxs", tokens.spacing.xxs),
			SpacingEntry("xs", tokens.spacing.xs),
			SpacingEntry("sm", tokens.spacing.sm),
			SpacingEntry("md", tokens.spacing.md),
			SpacingEntry("lg", tokens.spacing.lg),
			SpacingEntry("xl", tokens.spacing.xl),
			SpacingEntry("xxl", tokens.spacing.xxl),
			SpacingEntry("xxxl", tokens.spacing.xxxl),
			SpacingEntry("xxxxl", tokens.spacing.xxxxl),
		)
	ShowcaseSection("Spacing", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.sm.dp)) {
			entries.forEach { entry ->
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
				) {
					TextBlock(
						text = "${entry.label} (${entry.value.toInt()}dp)",
						variant = TextBlockVariant.LabelMedium,
						modifier = Modifier.width(100.dp),
					)
					Box(
						modifier =
							Modifier
								.width(entry.value.dp)
								.height(tokens.spacing.lg.dp)
								.clip(RoundedCornerShape(tokens.radius.xs.dp))
								.background(parseColor(tokens.color.primary)),
					)
				}
			}
		}
	}
}
