import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uikit.compose.components.primitives.divider.Divider
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.components.primitives.divider.DividerOrientation
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.tokens.DesignTokens

@Composable
internal fun DividerShowcase(tokens: DesignTokens) {
	ShowcaseSection("Разделитель (Divider)", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xl.dp)) {

			// Horizontal default
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				SubSectionTitle(text = "Horizontal", tokens = tokens)
				TextBlock(text = "Элемент выше", variant = TextBlockVariant.BodyMedium)
				Divider()
				TextBlock(text = "Элемент ниже", variant = TextBlockVariant.BodyMedium)
			}

			// Horizontal with insets
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				SubSectionTitle(text = "С отступами (inset)", tokens = tokens)
				TextBlock(text = "Элемент выше", variant = TextBlockVariant.BodyMedium)
				Divider(insetStart = tokens.spacing.xl, insetEnd = tokens.spacing.xl)
				TextBlock(text = "Элемент ниже", variant = TextBlockVariant.BodyMedium)
			}

			// Vertical
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				SubSectionTitle(text = "Vertical", tokens = tokens)
				Row(
					horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
					modifier = Modifier.height(IntrinsicSize.Min),
				) {
					TextBlock(text = "Слева", variant = TextBlockVariant.BodyMedium)
					Divider(orientation = DividerOrientation.Vertical)
					TextBlock(text = "Справа", variant = TextBlockVariant.BodyMedium)
				}
			}

			// Custom thickness
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				SubSectionTitle(text = "Толщина 3dp", tokens = tokens)
				Divider(thickness = 3.0)
			}
		}
	}
}
