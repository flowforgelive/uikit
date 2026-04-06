import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.foundation.TextEmphasis
import com.uikit.tokens.DesignTokens

@Composable
internal fun BreakpointsShowcase(tokens: DesignTokens) {
	val entries =
		listOf(
			"compact" to tokens.breakpoints.compact,
			"medium" to tokens.breakpoints.medium,
			"expanded" to tokens.breakpoints.expanded,
			"large" to tokens.breakpoints.large,
			"extraLarge" to tokens.breakpoints.extraLarge,
		)
	ShowcaseSection("Breakpoints (Material Design 3)", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.sm.dp)) {
			entries.forEach { (label, value) ->
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
				) {
					TextBlock(
						text = label,
						variant = TextBlockVariant.LabelMedium,
						modifier = Modifier.width(100.dp),
					)
					TextBlock(
						text = "${value.toInt()}dp",
						variant = TextBlockVariant.BodyLarge,
						emphasis = TextEmphasis.Primary,
					)
				}
			}
		}
	}
}
