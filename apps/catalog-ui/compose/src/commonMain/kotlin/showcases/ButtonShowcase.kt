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
import com.uikit.foundation.IconPosition
import com.uikit.foundation.VisualVariant
import com.uikit.tokens.DesignTokens
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon

private val ICON_POSITION_OPTIONS = listOf(
	"None" to "None",
	"Start" to "Start",
	"End" to "End",
	"Top" to "Top",
	"Bottom" to "Bottom",
	"StartEnd" to "Start+End",
)

private val STATE_OPTIONS = listOf(
	"active" to "Active",
	"disabled" to "Disabled",
	"loading" to "Loading",
)

private val INTENT_OPTIONS = listOf(
	"Primary" to "Primary",
	"Neutral" to "Neutral",
	"Danger" to "Danger",
)

private fun iconPositionFromId(id: String): IconPosition = when (id) {
	"Start" -> IconPosition.Start
	"End" -> IconPosition.End
	"Top" -> IconPosition.Top
	"Bottom" -> IconPosition.Bottom
	else -> IconPosition.None
}

private fun intentFromId(id: String): ColorIntent = when (id) {
	"Neutral" -> ColorIntent.Neutral
	"Danger" -> ColorIntent.Danger
	else -> ColorIntent.Primary
}

@Composable
internal fun ButtonShowcase(tokens: DesignTokens, globalSize: ComponentSize) {
	var selectedPositionId by remember { mutableStateOf("None") }
	var selectedStateId by remember { mutableStateOf("active") }
	var selectedIntentId by remember { mutableStateOf("Primary") }
	val selectedSize = buttonSizeFromComponentSize(globalSize)
	val selectedPosition = iconPositionFromId(selectedPositionId)
	val selectedIntent = intentFromId(selectedIntentId)
	val isStartEnd = selectedPositionId == "StartEnd"
	val hasIcons = selectedPositionId != "None"
	val isDisabled = selectedStateId == "disabled"
	val isLoading = selectedStateId == "loading"

	ShowcaseSection("Кнопка (Button)", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xl.dp)) {
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
				) {
					TextBlock(text = "Состояние:", variant = TextBlockVariant.LabelMedium)
					SegmentedControl(
						options = STATE_OPTIONS,
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
						options = INTENT_OPTIONS,
						selectedId = selectedIntentId,
						onSelectionChange = { selectedIntentId = it },
						modifier = Modifier.weight(1f),
					)
				}
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
				) {
					TextBlock(text = "Позиция иконки:", variant = TextBlockVariant.LabelMedium)
					SegmentedControl(
						options = ICON_POSITION_OPTIONS,
						selectedId = selectedPositionId,
						onSelectionChange = { selectedPositionId = it },
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
						if (hasIcons) {
							if (isStartEnd) {
								Button(
									text = "Start+End",
									variant = variant,
									intent = selectedIntent,
									size = selectedSize,
									iconPosition = IconPosition.Start,
									iconStart = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) },
									iconEnd = { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null) },
									disabled = isDisabled,
									loading = isLoading,
								)
							} else {
								Button(
									text = "Кнопка",
									variant = variant,
									intent = selectedIntent,
									size = selectedSize,
									iconPosition = selectedPosition,
									iconStart = if (selectedPosition != IconPosition.End) ({ Icon(Icons.Filled.Search, contentDescription = null) }) else null,
									iconEnd = if (selectedPosition == IconPosition.End) ({ Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) }) else null,
									disabled = isDisabled,
									loading = isLoading,
								)
							}
						} else {
							Button(
								text = "Кнопка",
								variant = variant,
								intent = selectedIntent,
								size = selectedSize,
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

