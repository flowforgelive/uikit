import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uikit.compose.components.composites.button.Button
import com.uikit.compose.components.primitives.badge.Badge
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.components.primitives.badge.BadgeVariant
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.foundation.ColorIntent
import com.uikit.foundation.TextEmphasis
import com.uikit.foundation.VisualVariant
import com.uikit.tokens.DesignTokens

private val INTENTS = listOf(
	"primary" to ColorIntent.Primary,
	"neutral" to ColorIntent.Neutral,
	"danger" to ColorIntent.Danger,
)

private val NUMERIC_VALUES = listOf(1, 5, 42, 99, 100)

@Composable
internal fun BadgeShowcase(tokens: DesignTokens) {
	ShowcaseSection("Значок (Badge)", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xl.dp)) {

			// Dot × intents
			SubSectionTitle(text = "Dot", tokens = tokens)
			Row(
				horizontalArrangement = Arrangement.spacedBy(tokens.spacing.lg.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				INTENTS.forEach { (label, intent) ->
					Column(
						horizontalAlignment = Alignment.CenterHorizontally,
						verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp),
					) {
						Badge(variant = BadgeVariant.Dot, intent = intent)
						TextBlock(
							text = label,
							variant = TextBlockVariant.LabelSmall,
							emphasis = TextEmphasis.Muted,
						)
					}
				}
			}

			// Numeric values
			SubSectionTitle(text = "Numeric", tokens = tokens)
			TextBlock(
				text = "Значения > maxValue (99) → «99+».",
				variant = TextBlockVariant.BodySmall,
				emphasis = TextEmphasis.Muted,
			)
			Row(
				horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				NUMERIC_VALUES.forEach { v ->
					Column(
						horizontalAlignment = Alignment.CenterHorizontally,
						verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp),
					) {
						Badge(variant = BadgeVariant.Numeric, value = v)
						TextBlock(
							text = "value=$v",
							variant = TextBlockVariant.LabelSmall,
							emphasis = TextEmphasis.Muted,
						)
					}
				}
			}

			// All intents × numeric
			SubSectionTitle(text = "Все intent × numeric", tokens = tokens)
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				INTENTS.forEach { (label, intent) ->
					Row(
						horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
						verticalAlignment = Alignment.CenterVertically,
					) {
						TextBlock(
							text = label,
							variant = TextBlockVariant.LabelMedium,
							modifier = Modifier.size(width = 60.dp, height = 20.dp),
						)
						Badge(variant = BadgeVariant.Numeric, value = 3, intent = intent)
						Badge(variant = BadgeVariant.Numeric, value = 42, intent = intent)
						Badge(variant = BadgeVariant.Numeric, value = 100, intent = intent)
					}
				}
			}

			// Positioning on icon button
			SubSectionTitle(text = "Позиционирование на кнопке", tokens = tokens)
			TextBlock(
				text = "Badge позиционируется через Box + offset поверх кнопки-иконки.",
				variant = TextBlockVariant.BodySmall,
				emphasis = TextEmphasis.Muted,
			)
			Row(
				horizontalArrangement = Arrangement.spacedBy(tokens.spacing.xl.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				// Dot on icon button
				Box {
					Button(
						icon = { Icon(Icons.Filled.Star, contentDescription = null) },
						variant = VisualVariant.Ghost,
						intent = ColorIntent.Neutral,
					)
					Badge(
						variant = BadgeVariant.Dot,
						modifier = Modifier.align(Alignment.TopEnd).offset(x = 2.dp, y = (-2).dp),
					)
				}

				// Numeric on icon button
				Box {
					Button(
						icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
						variant = VisualVariant.Ghost,
						intent = ColorIntent.Neutral,
					)
					Badge(
						variant = BadgeVariant.Numeric,
						value = 5,
						modifier = Modifier.align(Alignment.TopEnd).offset(x = 6.dp, y = (-4).dp),
					)
				}

				// Large numeric on icon button
				Box {
					Button(
						icon = { Icon(Icons.Filled.Search, contentDescription = null) },
						variant = VisualVariant.Ghost,
						intent = ColorIntent.Neutral,
					)
					Badge(
						variant = BadgeVariant.Numeric,
						value = 128,
						modifier = Modifier.align(Alignment.TopEnd).offset(x = 10.dp, y = (-4).dp),
					)
				}
			}
		}
	}
}
