import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.text.TextStyle
import com.uikit.compose.components.atoms.iconbutton.IconButton
import com.uikit.compose.theme.parseColor
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

	ShowcaseSection("Кнопка-иконка (Icon Button)", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xl.dp)) {

			VisualVariant.entries.forEach { variant ->
				Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
					SubSectionTitle(text = variant.name, tokens = tokens)
					ColorIntent.entries.forEach { intent ->
						Column {
						BasicText(
							text = "${intent.name}:",
							style = TextStyle(
								fontSize = tokens.typography.labelMedium.fontSize.sp,
								lineHeight = tokens.typography.labelMedium.lineHeight.sp,
								letterSpacing = tokens.typography.labelMedium.letterSpacing.sp,
								color = parseColor(tokens.color.textMuted),
							),
							)
							Row(
								horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
								verticalAlignment = Alignment.CenterVertically,
								modifier = Modifier.horizontalScroll(rememberScrollState()),
							) {
								ICON_BUTTON_SAMPLES.forEach { iconSlot ->
									IconButton(
										icon = iconSlot,
										variant = variant,
										intent = intent,
										size = selectedSize,
									)
								}
								IconButton(
									icon = { Icon(Icons.Filled.Star, contentDescription = null) },
									variant = variant,
									intent = intent,
									size = selectedSize,
									disabled = true,
								)
								IconButton(
									icon = { Icon(Icons.Filled.Star, contentDescription = null) },
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
