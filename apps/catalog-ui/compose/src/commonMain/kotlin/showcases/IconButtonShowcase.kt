import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uikit.compose.components.composites.button.Button
import com.uikit.compose.components.composites.segmentedcontrol.SegmentedControl
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

private val IB_STATE_OPTIONS = listOf(
	"active" to "Active",
	"disabled" to "Disabled",
	"loading" to "Loading",
)

private val IB_INTENT_OPTIONS = listOf(
	"Primary" to "Primary",
	"Neutral" to "Neutral",
	"Danger" to "Danger",
)

private fun ibIntentFromId(id: String): ColorIntent = when (id) {
	"Neutral" -> ColorIntent.Neutral
	"Danger" -> ColorIntent.Danger
	else -> ColorIntent.Primary
}

@Composable
internal fun IconButtonShowcase(tokens: DesignTokens, globalSize: ComponentSize) {
	val selectedSize = globalSize
	var selectedStateId by remember { mutableStateOf("active") }
	var selectedIntentId by remember { mutableStateOf("Primary") }
	val selectedIntent = ibIntentFromId(selectedIntentId)
	val isDisabled = selectedStateId == "disabled"
	val isLoading = selectedStateId == "loading"

	ShowcaseSection("Кнопка-иконка (Button icon-only)", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xl.dp)) {

			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
				) {
					TextBlock(text = "Состояние:", variant = TextBlockVariant.LabelMedium)
					SegmentedControl(
						options = IB_STATE_OPTIONS,
						selectedId = selectedStateId,
						onSelectionChange = { selectedStateId = it },
						modifier = Modifier.weight(1f),
					)
				}
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
				) {
					TextBlock(text = "Intent:", variant = TextBlockVariant.LabelMedium)
					SegmentedControl(
						options = IB_INTENT_OPTIONS,
						selectedId = selectedIntentId,
						onSelectionChange = { selectedIntentId = it },
						modifier = Modifier.weight(1f),
					)
				}
			}

			VisualVariant.entries.forEach { variant ->
				Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
					SubSectionTitle(text = variant.name, tokens = tokens)
					Row(
						horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier.horizontalScroll(rememberScrollState()),
					) {
						ICON_BUTTON_SAMPLES.forEach { iconSlot ->
							Button(
								icon = iconSlot,
								variant = variant,
								intent = selectedIntent,
								size = selectedSize,
								ariaLabel = "Icon button",
								disabled = isDisabled,
								loading = isLoading,
							)
						}
					}
				}
			}
		}
	}
}
