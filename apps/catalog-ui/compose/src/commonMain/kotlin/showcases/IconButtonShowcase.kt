import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uikit.compose.components.composites.button.Button
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.foundation.ColorIntent
import com.uikit.foundation.ComponentSize
import com.uikit.foundation.VisualVariant
import com.uikit.tokens.DesignTokens
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon

private val ICON_BUTTON_SAMPLES: List<@Composable () -> Unit> = listOf(
	{ Icon(Icons.Filled.Search, contentDescription = null) },
	{ Icon(Icons.Filled.Add, contentDescription = null) },
	{ Icon(Icons.Filled.Star, contentDescription = null) },
	{ Icon(Icons.Filled.Settings, contentDescription = null) },
	{ Icon(Icons.Filled.Close, contentDescription = null) },
	{ Icon(Icons.Filled.Check, contentDescription = null) },
)

@Composable
internal fun IconButtonShowcase(tokens: DesignTokens, globalSize: ComponentSize) {
	val selectedSize = globalSize

	ShowcaseSection("Кнопка-иконка (Button icon-only)", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xl.dp)) {

			VisualVariant.entries.forEach { variant ->
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
								ICON_BUTTON_SAMPLES.forEach { iconSlot ->
									Button(
										icon = iconSlot,
										variant = variant,
										intent = intent,
										size = selectedSize,
										ariaLabel = "Icon button",
									)
								}
								Button(
									icon = { Icon(Icons.Filled.Star, contentDescription = null) },
									variant = variant,
									intent = intent,
									size = selectedSize,
									ariaLabel = "Disabled",
									disabled = true,
								)
								Button(
									icon = { Icon(Icons.Filled.Star, contentDescription = null) },
									variant = variant,
									intent = intent,
									size = selectedSize,
									ariaLabel = "Loading",
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
