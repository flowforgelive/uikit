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

private fun iconPositionFromId(id: String): IconPosition = when (id) {
	"Start" -> IconPosition.Start
	"End" -> IconPosition.End
	"Top" -> IconPosition.Top
	"Bottom" -> IconPosition.Bottom
	else -> IconPosition.None
}

@Composable
internal fun ButtonShowcase(tokens: DesignTokens, globalSize: ComponentSize) {
	var selectedPositionId by remember { mutableStateOf("None") }
	val selectedSize = buttonSizeFromComponentSize(globalSize)
	val selectedPosition = iconPositionFromId(selectedPositionId)
	val isStartEnd = selectedPositionId == "StartEnd"
	val hasIcons = selectedPositionId != "None"

	ShowcaseSection("Кнопка (Button)", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xl.dp)) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
			) {
				TextBlock(
					text = "Позиция иконки:",
					variant = TextBlockVariant.LabelMedium,
				)
				SegmentedControl(
					options = ICON_POSITION_OPTIONS,
					selectedId = selectedPositionId,
					onSelectionChange = { selectedPositionId = it },
					modifier = Modifier.weight(1f),
				)
			}

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
								if (hasIcons) {
									if (isStartEnd) {
										Button(
											text = "Start+End",
											variant = variant,
											intent = intent,
											size = selectedSize,
											iconPosition = IconPosition.Start,
											iconStart = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) },
											iconEnd = { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null) },
										)
									} else {
										Button(
											text = "Кнопка",
											variant = variant,
											intent = intent,
											size = selectedSize,
											iconPosition = selectedPosition,
											iconStart = if (selectedPosition != IconPosition.End) ({ Icon(Icons.Filled.Search, contentDescription = null) }) else null,
											iconEnd = if (selectedPosition == IconPosition.End) ({ Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) }) else null,
										)
									}
									Button(
										text = "Disabled",
										variant = variant,
										intent = intent,
										size = selectedSize,
										iconPosition = if (isStartEnd) IconPosition.Start else selectedPosition,
										iconStart = { Icon(Icons.Filled.Star, contentDescription = null) },
										disabled = true,
									)
									Button(
										text = "Loading",
										variant = variant,
										intent = intent,
										size = selectedSize,
										iconPosition = selectedPosition,
										iconStart = { Icon(Icons.Filled.Check, contentDescription = null) },
										loading = true,
									)
								} else {
									Button(
										text = "Кнопка",
										variant = variant,
										intent = intent,
										size = selectedSize,
									)
									Button(text = "Disabled", variant = variant, intent = intent, size = selectedSize, disabled = true)
									Button(text = "Loading", variant = variant, intent = intent, size = selectedSize, loading = true)
								}
								}
							}
						}
					}
				}
			}
		}
	}

