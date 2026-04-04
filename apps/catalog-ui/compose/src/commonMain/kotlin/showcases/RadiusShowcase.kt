import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.text.TextStyle
import com.uikit.compose.theme.parseColor
import com.uikit.tokens.DesignTokens

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun RadiusShowcase(tokens: DesignTokens) {
	val entries =
		listOf(
			"xs" to tokens.radius.xs,
			"sm" to tokens.radius.sm,
			"md" to tokens.radius.md,
			"lg" to tokens.radius.lg,
			"xl" to tokens.radius.xl,
			"full" to tokens.radius.full,
		)
	ShowcaseSection("Radius", tokens) {
		FlowRow(
			horizontalArrangement = Arrangement.spacedBy(tokens.spacing.lg.dp),
			verticalArrangement = Arrangement.spacedBy(tokens.spacing.lg.dp),
		) {
			entries.forEach { (label, value) ->
				Column(horizontalAlignment = Alignment.CenterHorizontally) {
					Box(
						modifier =
							Modifier
								.size(56.dp)
								.clip(RoundedCornerShape(value.dp))
								.background(parseColor(tokens.color.primary)),
					)
					Spacer(Modifier.height(tokens.spacing.xs.dp))
					BasicText(
						text = "$label (${value.toInt()}dp)",
						style = TextStyle(
							fontSize = tokens.typography.labelMedium.fontSize.sp,
							lineHeight = tokens.typography.labelMedium.lineHeight.sp,
							letterSpacing = tokens.typography.labelMedium.letterSpacing.sp,
							color = parseColor(tokens.color.textMuted),
						),
					)
				}
			}
		}
	}
}
