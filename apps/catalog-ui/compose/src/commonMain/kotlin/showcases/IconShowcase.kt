import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.uikit.compose.components.primitives.icon.Icon
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.foundation.ComponentSize
import com.uikit.tokens.DesignTokens
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star

private val ICON_SAMPLES: List<Pair<String, @Composable () -> Unit>> = listOf(
	"Search" to { androidx.compose.material3.Icon(Icons.Filled.Search, contentDescription = null) },
	"Add" to { androidx.compose.material3.Icon(Icons.Filled.Add, contentDescription = null) },
	"Star" to { androidx.compose.material3.Icon(Icons.Filled.Star, contentDescription = null) },
	"Settings" to { androidx.compose.material3.Icon(Icons.Filled.Settings, contentDescription = null) },
	"Close" to { androidx.compose.material3.Icon(Icons.Filled.Close, contentDescription = null) },
	"Check" to { androidx.compose.material3.Icon(Icons.Filled.Check, contentDescription = null) },
)

@Composable
internal fun IconShowcase(tokens: DesignTokens) {
	ShowcaseSection("Иконки (Icon)", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xl.dp)) {

			// Size variants
			ComponentSize.entries.forEach { size ->
				Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
					SubSectionTitle(text = size.name, tokens = tokens)
					Row(
						horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
					) {
						ICON_SAMPLES.forEach { (_, content) ->
							Icon(size = size) { content() }
						}
					}
				}
			}

			// Custom color
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				SubSectionTitle(text = "Кастомный цвет", tokens = tokens)
				Row(horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
					Icon(color = tokens.color.primary) {
						androidx.compose.material3.Icon(Icons.Filled.Star, contentDescription = null)
					}
					Icon(color = tokens.color.danger) {
						androidx.compose.material3.Icon(Icons.Filled.Close, contentDescription = null)
					}
					Icon(color = tokens.color.textSecondary) {
						androidx.compose.material3.Icon(Icons.Filled.Settings, contentDescription = null)
					}
				}
			}
		}
	}
}
