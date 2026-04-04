import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.text.TextStyle as ComposeTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uikit.compose.theme.parseColor
import com.uikit.tokens.DesignTokens
import com.uikit.tokens.TextStyle

private data class TypoEntry(val key: String, val label: String)

private val TYPO_ENTRIES =
	listOf(
		TypoEntry("displayLarge", "Display Large"),
		TypoEntry("displayMedium", "Display Medium"),
		TypoEntry("displaySmall", "Display Small"),
		TypoEntry("headlineLarge", "Headline Large"),
		TypoEntry("headlineMedium", "Headline Medium"),
		TypoEntry("headlineSmall", "Headline Small"),
		TypoEntry("titleLarge", "Title Large"),
		TypoEntry("titleMedium", "Title Medium"),
		TypoEntry("titleSmall", "Title Small"),
		TypoEntry("bodyLarge", "Body Large"),
		TypoEntry("bodyMedium", "Body Medium"),
		TypoEntry("bodySmall", "Body Small"),
		TypoEntry("labelLarge", "Label Large"),
		TypoEntry("labelMedium", "Label Medium"),
		TypoEntry("labelSmall", "Label Small"),
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
					BasicText(
						text = entry.label,
						style = ComposeTextStyle(
							fontSize = style.fontSize.sp,
							fontWeight = FontWeight(style.fontWeight),
							letterSpacing = style.letterSpacing.sp,
							lineHeight = style.lineHeight.sp,
							color = parseColor(tokens.color.textPrimary),
						),
						modifier = Modifier.alignByBaseline(),
					)
					BasicText(
						text = "${style.fontSize.toInt()}dp / ${style.fontWeight} / ${style.lineHeight.toInt()}dp",
						style = ComposeTextStyle(
							fontSize = tokens.typography.labelMedium.fontSize.sp,
							lineHeight = tokens.typography.labelMedium.lineHeight.sp,
							letterSpacing = tokens.typography.labelMedium.letterSpacing.sp,
							color = parseColor(tokens.color.textMuted),
						),
						modifier = Modifier.alignByBaseline(),
					)
				}
			}
		}
	}
}
