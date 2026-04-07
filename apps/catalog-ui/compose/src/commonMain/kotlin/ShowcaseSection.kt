import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.CatalogLayoutResolver
import com.uikit.compose.components.primitives.divider.Divider
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.tokens.DesignTokens

@Composable
internal fun ShowcaseSection(
	title: String,
	tokens: DesignTokens,
	content: @Composable () -> Unit,
) {
	val layout = remember(tokens) { CatalogLayoutResolver.resolve(tokens) }

	Column(modifier = Modifier.fillMaxWidth()) {
		Row(
			modifier = Modifier.fillMaxWidth().padding(bottom = layout.sectionTitleMarginBottom.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(layout.sectionTitleGap.dp),
		) {
			TextBlock(text = title, variant = TextBlockVariant.HeadlineMedium)
			Box(modifier = Modifier.weight(1f)) {
				Divider()
			}
		}
		content()
	}
}
