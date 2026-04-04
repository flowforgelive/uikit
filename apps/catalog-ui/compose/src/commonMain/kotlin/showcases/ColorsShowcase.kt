import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.text.TextStyle
import com.uikit.compose.theme.parseColor
import com.uikit.tokens.DesignTokens

private data class ColorEntry(val key: String, val label: String)

private val COLOR_ENTRIES =
	listOf(
		ColorEntry("primary", "Primary"),
		ColorEntry("primaryHover", "Primary Hover"),
		ColorEntry("secondary", "Secondary"),
		ColorEntry("danger", "Danger"),
		ColorEntry("dangerHover", "Danger Hover"),
		ColorEntry("dangerSoft", "Danger Soft"),
		ColorEntry("dangerSoftHover", "Danger Soft Hover"),
		ColorEntry("background", "Background"),
		ColorEntry("surface", "Surface"),
		ColorEntry("surfaceContainerLowest", "Container Lowest"),
		ColorEntry("surfaceContainerLow", "Container Low"),
		ColorEntry("surfaceContainer", "Surface Container"),
		ColorEntry("surfaceContainerHigh", "Container High"),
		ColorEntry("surfaceContainerHighest", "Container Highest"),
		ColorEntry("surfaceHover", "Surface Hover"),
		ColorEntry("onSurface", "On Surface"),
		ColorEntry("outline", "Outline"),
		ColorEntry("outlineVariant", "Outline Variant"),
		ColorEntry("textPrimary", "Text Primary"),
		ColorEntry("textSecondary", "Text Secondary"),
		ColorEntry("textMuted", "Text Muted"),
		ColorEntry("textOnPrimary", "Text on Primary"),
		ColorEntry("textOnDanger", "Text on Danger"),
		ColorEntry("textDisabled", "Text Disabled"),
		ColorEntry("surfaceDisabled", "Surface Disabled"),
		ColorEntry("borderDisabled", "Border Disabled"),
		ColorEntry("border", "Border"),
	)

private fun getColorValue(
	tokens: DesignTokens,
	key: String,
) = when (key) {
	"primary" -> tokens.color.primary
	"primaryHover" -> tokens.color.primaryHover
	"secondary" -> tokens.color.secondary
	"danger" -> tokens.color.danger
	"dangerHover" -> tokens.color.dangerHover
	"dangerSoft" -> tokens.color.dangerSoft
	"dangerSoftHover" -> tokens.color.dangerSoftHover
	"background" -> tokens.color.background
	"surface" -> tokens.color.surface
	"surfaceContainerLowest" -> tokens.color.surfaceContainerLowest
	"surfaceContainerLow" -> tokens.color.surfaceContainerLow
	"surfaceContainer" -> tokens.color.surfaceContainer
	"surfaceContainerHigh" -> tokens.color.surfaceContainerHigh
	"surfaceContainerHighest" -> tokens.color.surfaceContainerHighest
	"surfaceHover" -> tokens.color.surfaceHover
	"onSurface" -> tokens.color.onSurface
	"outline" -> tokens.color.outline
	"outlineVariant" -> tokens.color.outlineVariant
	"textPrimary" -> tokens.color.textPrimary
	"textSecondary" -> tokens.color.textSecondary
	"textMuted" -> tokens.color.textMuted
	"textOnPrimary" -> tokens.color.textOnPrimary
	"textOnDanger" -> tokens.color.textOnDanger
	"textDisabled" -> tokens.color.textDisabled
	"surfaceDisabled" -> tokens.color.surfaceDisabled
	"borderDisabled" -> tokens.color.borderDisabled
	"border" -> tokens.color.border
	else -> tokens.color.primary
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ColorsShowcase(tokens: DesignTokens) {
	ShowcaseSection("Colors", tokens) {
		FlowRow(
			horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
			verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
		) {
			COLOR_ENTRIES.forEach { entry ->
				val hex = getColorValue(tokens, entry.key)
				Column(
					modifier = Modifier.width(120.dp),
					verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp),
				) {
					Box(
						modifier =
							Modifier
								.fillMaxWidth()
								.aspectRatio(1f)
								.clip(RoundedCornerShape(tokens.radius.md.dp))
								.background(parseColor(hex))
								.border(1.dp, parseColor(tokens.color.outlineVariant), RoundedCornerShape(tokens.radius.md.dp)),
					)
					BasicText(
						text = entry.label,
						style = TextStyle(
							fontSize = tokens.typography.labelSmall.fontSize.sp,
							fontWeight = FontWeight(600),
							lineHeight = tokens.typography.labelSmall.lineHeight.sp,
							letterSpacing = tokens.typography.labelSmall.letterSpacing.sp,
							color = parseColor(tokens.color.textPrimary),
						),
					)
					BasicText(
						text = hex,
						style = TextStyle(
							fontSize = tokens.typography.labelSmall.fontSize.sp,
							lineHeight = tokens.typography.labelSmall.lineHeight.sp,
							letterSpacing = tokens.typography.labelSmall.letterSpacing.sp,
							color = parseColor(tokens.color.textMuted),
						),
					)
				}
			}
		}
	}
}
