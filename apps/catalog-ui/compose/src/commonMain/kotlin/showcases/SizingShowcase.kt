import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.compose.theme.parseColor
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.tokens.DesignTokens

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SizingShowcase(tokens: DesignTokens) {
	val btnSizes =
		listOf(
			Triple("XS", tokens.sizing.controlXs, tokens.sizing.controlXs * 2),
			Triple("SM", tokens.sizing.controlSm, tokens.sizing.controlSm * 2),
			Triple("MD", tokens.sizing.controlMd, tokens.sizing.controlMd * 2),
			Triple("LG", tokens.sizing.controlLg, tokens.sizing.controlLg * 2),
			Triple("XL", tokens.sizing.controlXl, tokens.sizing.controlXl * 2),
		)
	val iconSizes =
		listOf(
			"XS" to tokens.sizing.iconXs,
			"SM" to tokens.sizing.iconSm,
			"MD" to tokens.sizing.iconMd,
			"LG" to tokens.sizing.iconLg,
			"XL" to tokens.sizing.iconXl,
		)

	ShowcaseSection("Sizing", tokens) {
		SubSectionTitle(text = "Button Heights", tokens = tokens)
		FlowRow(
			horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
			verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
		) {
			btnSizes.forEach { (label, h, w) ->
				Column(horizontalAlignment = Alignment.CenterHorizontally) {
					Box(
						modifier =
							Modifier
								.width(w.dp)
								.height(h.dp)
								.clip(RoundedCornerShape(tokens.radius.md.dp))
								.background(parseColor(tokens.color.primary)),
						contentAlignment = Alignment.Center,
					) {
						BasicText(
							text = "${h.toInt()}dp",
							style = TextStyle(
								fontSize = tokens.typography.labelSmall.fontSize.sp,
								fontWeight = FontWeight.SemiBold,
								lineHeight = tokens.typography.labelSmall.lineHeight.sp,
								letterSpacing = tokens.typography.labelSmall.letterSpacing.sp,
								color = parseColor(tokens.color.textOnPrimary),
							),
						)
					}
					Spacer(Modifier.height(tokens.spacing.xs.dp))
					TextBlock(
						text = label,
						variant = TextBlockVariant.LabelMedium,
					)
				}
			}
		}

		Spacer(Modifier.height(tokens.spacing.xl.dp))

		SubSectionTitle(text = "Icon Sizes", tokens = tokens)
		FlowRow(
			horizontalArrangement = Arrangement.spacedBy(tokens.spacing.lg.dp),
			verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
		) {
			iconSizes.forEach { (label, size) ->
				Column(horizontalAlignment = Alignment.CenterHorizontally) {
					Box(
						modifier =
							Modifier
								.size(size.dp)
								.clip(RoundedCornerShape(tokens.radius.sm.dp))
								.background(parseColor(tokens.color.primary)),
					)
					Spacer(Modifier.height(tokens.spacing.xs.dp))
					TextBlock(
						text = "$label (${size.toInt()}dp)",
						variant = TextBlockVariant.LabelMedium,
					)
				}
			}
		}

		Spacer(Modifier.height(tokens.spacing.xl.dp))

		// Min Touch Target
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
			modifier =
				Modifier
					.border(1.dp, parseColor(tokens.color.outlineVariant), RoundedCornerShape(tokens.radius.md.dp))
					.padding(tokens.spacing.md.dp),
		) {
			Box(
				modifier =
					Modifier
						.size(tokens.sizing.minTouchTarget.dp)
						.border(2.dp, parseColor(tokens.color.danger), RoundedCornerShape(tokens.radius.sm.dp)),
				contentAlignment = Alignment.Center,
			) {
					BasicText(
						text = "${tokens.sizing.minTouchTarget.toInt()}dp",
						style = TextStyle(
							fontSize = tokens.typography.labelSmall.fontSize.sp,
							fontWeight = FontWeight.SemiBold,
							lineHeight = tokens.typography.labelSmall.lineHeight.sp,
							letterSpacing = tokens.typography.labelSmall.letterSpacing.sp,
							color = parseColor(tokens.color.danger),
						),
				)
			}
			TextBlock(
				text = "Min Touch Target (Apple HIG 44dp / Material 48dp)",
				variant = TextBlockVariant.BodySmall,
			)
		}
	}
}
