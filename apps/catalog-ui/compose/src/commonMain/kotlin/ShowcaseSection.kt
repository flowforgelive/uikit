import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import catalog.CatalogLayoutResolver
import com.uikit.compose.theme.parseColor
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
			BasicText(
				text = title,
				style = TextStyle(
					fontSize = tokens.typography.headlineMedium.fontSize.sp,
					fontWeight = FontWeight(tokens.typography.headlineMedium.fontWeight),
					lineHeight = tokens.typography.headlineMedium.lineHeight.sp,
					letterSpacing = tokens.typography.headlineMedium.letterSpacing.sp,
					color = parseColor(tokens.color.textPrimary),
				),
			)
			Box(modifier = Modifier.weight(1f).height(1.dp).background(parseColor(tokens.color.outlineVariant)))
		}
		content()
	}
}
