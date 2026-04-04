import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.text.TextStyle
import com.uikit.compose.theme.parseColor
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
					BasicText(
						text = label,
						style = TextStyle(
							fontSize = tokens.typography.labelMedium.fontSize.sp,
							lineHeight = tokens.typography.labelMedium.lineHeight.sp,
							letterSpacing = tokens.typography.labelMedium.letterSpacing.sp,
							color = parseColor(tokens.color.textMuted),
							textAlign = TextAlign.End,
						),
						modifier = Modifier.width(100.dp),
					)
					BasicText(
						text = "${value.toInt()}dp",
						style = TextStyle(
							fontSize = tokens.typography.bodyLarge.fontSize.sp,
							fontWeight = FontWeight.SemiBold,
							lineHeight = tokens.typography.bodyLarge.lineHeight.sp,
							letterSpacing = tokens.typography.bodyLarge.letterSpacing.sp,
							color = parseColor(tokens.color.textPrimary),
						),
					)
				}
			}
		}
	}
}
