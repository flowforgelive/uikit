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
import com.uikit.compose.components.composites.chip.Chip
import com.uikit.compose.components.composites.segmentedcontrol.SegmentedControl
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

private val CHIP_MODE_OPTIONS = listOf(
	"static" to "Static",
	"interactive" to "Interactive",
	"selected" to "Selected",
	"disabled" to "Disabled",
)

private val CHIP_INTENT_OPTIONS = listOf(
	"Primary" to "Primary",
	"Neutral" to "Neutral",
	"Danger" to "Danger",
)

private fun chipIntentFromId(id: String): ColorIntent = when (id) {
	"Neutral" -> ColorIntent.Neutral
	"Danger" -> ColorIntent.Danger
	else -> ColorIntent.Primary
}

@Composable
internal fun ChipShowcase(tokens: DesignTokens, globalSize: ComponentSize) {
	val selectedSize = globalSize
	var selectedModeId by remember { mutableStateOf("static") }
	var selectedIntentId by remember { mutableStateOf("Primary") }
	val selectedIntent = chipIntentFromId(selectedIntentId)
	val isStatic = selectedModeId == "static"
	val isDisabled = selectedModeId == "disabled"
	val isSelected = selectedModeId == "selected"

	ShowcaseSection("Чип (Chip)", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xl.dp)) {

			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
				) {
					TextBlock(text = "Режим:", variant = TextBlockVariant.LabelMedium)
					SegmentedControl(
						options = CHIP_MODE_OPTIONS,
						selectedId = selectedModeId,
						onSelectionChange = { selectedModeId = it },
						modifier = Modifier.weight(1f),
					)
				}
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
				) {
					TextBlock(text = "Intent:", variant = TextBlockVariant.LabelMedium)
					SegmentedControl(
						options = CHIP_INTENT_OPTIONS,
						selectedId = selectedIntentId,
						onSelectionChange = { selectedIntentId = it },
						modifier = Modifier.weight(1f),
					)
				}
			}

			CHIP_VARIANTS.forEach { variant ->
				Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
					SubSectionTitle(text = variant.name, tokens = tokens)
					Row(
						horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier.horizontalScroll(rememberScrollState()),
					) {
						val onClick: (() -> Unit)? = if (isStatic) null else ({})
						Chip(
							text = "Chip",
							variant = variant,
							intent = selectedIntent,
							size = selectedSize,
							onClick = onClick,
							disabled = isDisabled,
							selected = isSelected,
						)
						Chip(
							text = "С иконкой",
							variant = variant,
							intent = selectedIntent,
							size = selectedSize,
							leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
							onClick = onClick,
							disabled = isDisabled,
							selected = isSelected,
						)
						Chip(
							text = "Dismissible",
							variant = variant,
							intent = selectedIntent,
							size = selectedSize,
							dismissible = true,
							onDismiss = {},
							onClick = onClick,
							disabled = isDisabled,
							selected = isSelected,
						)
						Chip(
							text = "Icon + Dismiss",
							variant = variant,
							intent = selectedIntent,
							size = selectedSize,
							leadingIcon = { Icon(Icons.Filled.Star, contentDescription = null) },
							dismissible = true,
							onDismiss = {},
							onClick = onClick,
							disabled = isDisabled,
							selected = isSelected,
						)
					}
				}
			}
		}
	}
}
