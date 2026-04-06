import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.unit.dp
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.tokens.DesignTokens
import com.uikit.tokens.TextStyle

private data class TypoEntry(val key: String, val label: String, val variant: TextBlockVariant)

private val TYPO_ENTRIES =
	listOf(
		TypoEntry("displayLarge", "Display Large", TextBlockVariant.DisplayLarge),
		TypoEntry("displayMedium", "Display Medium", TextBlockVariant.DisplayMedium),
		TypoEntry("displaySmall", "Display Small", TextBlockVariant.DisplaySmall),
		TypoEntry("headlineLarge", "Headline Large", TextBlockVariant.HeadlineLarge),
		TypoEntry("headlineMedium", "Headline Medium", TextBlockVariant.HeadlineMedium),
		TypoEntry("headlineSmall", "Headline Small", TextBlockVariant.HeadlineSmall),
		TypoEntry("titleLarge", "Title Large", TextBlockVariant.TitleLarge),
		TypoEntry("titleMedium", "Title Medium", TextBlockVariant.TitleMedium),
		TypoEntry("titleSmall", "Title Small", TextBlockVariant.TitleSmall),
		TypoEntry("bodyLarge", "Body Large", TextBlockVariant.BodyLarge),
		TypoEntry("bodyMedium", "Body Medium", TextBlockVariant.BodyMedium),
		TypoEntry("bodySmall", "Body Small", TextBlockVariant.BodySmall),
		TypoEntry("labelLarge", "Label Large", TextBlockVariant.LabelLarge),
		TypoEntry("labelMedium", "Label Medium", TextBlockVariant.LabelMedium),
		TypoEntry("labelSmall", "Label Small", TextBlockVariant.LabelSmall),
	)

private fun getTypoStyle(
	tokens: DesignTokens,
	key: String,
): TextStyle = when (key) {
	"displayLarge" -> tokens.typography.displayLarge
	"displayMedium" -> tokens.typography.displayMedium
	"displaySmall" -> tokens.typography.displaySmall
	"headlineLarge" -> tokens.typography.headlineLarge
	"headlineMedium" -> tokens.typography.headlineMedium
	"headlineSmall" -> tokens.typography.headlineSmall
	"titleLarge" -> tokens.typography.titleLarge
	"titleMedium" -> tokens.typography.titleMedium
	"titleSmall" -> tokens.typography.titleSmall
	"bodyLarge" -> tokens.typography.bodyLarge
	"bodyMedium" -> tokens.typography.bodyMedium
	"bodySmall" -> tokens.typography.bodySmall
	"labelLarge" -> tokens.typography.labelLarge
	"labelMedium" -> tokens.typography.labelMedium
	"labelSmall" -> tokens.typography.labelSmall
	else -> tokens.typography.bodyLarge
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TypographyShowcase(tokens: DesignTokens) {
	ShowcaseSection("Typography", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
			TYPO_ENTRIES.forEach { entry ->
				val style = getTypoStyle(tokens, entry.key)
				FlowRow(
					horizontalArrangement = Arrangement.spacedBy(tokens.spacing.lg.dp),
				) {
					TextBlock(
						text = entry.label,
						variant = entry.variant,
						modifier = Modifier.alignByBaseline(),
					)
					TextBlock(
						text = "${style.fontSize.toInt()}dp / ${style.fontWeight} / ${style.lineHeight.toInt()}dp",
						variant = TextBlockVariant.LabelMedium,
						modifier = Modifier.alignByBaseline(),
					)
				}
			}
		}
	}
}
