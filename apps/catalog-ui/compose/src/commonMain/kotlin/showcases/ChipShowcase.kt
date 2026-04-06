import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uikit.compose.components.composites.chip.Chip
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.foundation.ColorIntent
import com.uikit.foundation.ComponentSize
import com.uikit.foundation.VisualVariant
import com.uikit.tokens.DesignTokens
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon

private val CHIP_VARIANTS = listOf(VisualVariant.Soft, VisualVariant.Outline, VisualVariant.Ghost)

@Composable
internal fun ChipShowcase(tokens: DesignTokens, globalSize: ComponentSize) {
	val selectedSize = globalSize

	ShowcaseSection("Чип (Chip)", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xl.dp)) {

			CHIP_VARIANTS.forEach { variant ->
				Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
					SubSectionTitle(text = variant.name, tokens = tokens)
					ColorIntent.entries.forEach { intent ->
						Column {
							TextBlock(
								text = "${intent.name}:",
								variant = TextBlockVariant.LabelMedium,
							)
							Row(
								horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
								verticalAlignment = Alignment.CenterVertically,
								modifier = Modifier.horizontalScroll(rememberScrollState()),
							) {
								Chip(
									text = "Chip",
									variant = variant,
									intent = intent,
									size = selectedSize,
								)
								Chip(
									text = "С иконкой",
									variant = variant,
									intent = intent,
									size = selectedSize,
									leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
								)
								Chip(
									text = "Dismissible",
									variant = variant,
									intent = intent,
									size = selectedSize,
									dismissible = true,
									onDismiss = {},
								)
								Chip(
									text = "Icon + Dismiss",
									variant = variant,
									intent = intent,
									size = selectedSize,
									leadingIcon = { Icon(Icons.Filled.Star, contentDescription = null) },
									dismissible = true,
									onDismiss = {},
								)
								Chip(
									text = "Selected",
									variant = variant,
									intent = intent,
									size = selectedSize,
									selected = true,
								)
								Chip(
									text = "Disabled",
									variant = variant,
									intent = intent,
									size = selectedSize,
									disabled = true,
								)
								Chip(
									text = "Loading",
									variant = variant,
									intent = intent,
									size = selectedSize,
									loading = true,
								)
							}
						}
					}
				}
			}
		}
	}
}
