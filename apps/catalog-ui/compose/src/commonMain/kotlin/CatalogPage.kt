import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import catalog.CatalogLayoutResolver
import com.uikit.compose.components.atoms.button.Button
import com.uikit.compose.components.atoms.text.TextBlock
import com.uikit.components.atoms.text.TextBlockVariant
import com.uikit.compose.theme.parseColor
import com.uikit.foundation.VisualVariant
import com.uikit.tokens.DesignTokens

@Composable
internal fun CatalogPage(
	title: String,
	subtitle: String,
	tokens: DesignTokens,
	topBarEnd: @Composable () -> Unit,
	onBack: () -> Unit,
	content: @Composable () -> Unit,
) {
	val layout = remember(tokens) { CatalogLayoutResolver.resolve(tokens) }

	Column(
		modifier =
			Modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState()),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		// Top bar
		Row(
			modifier =
				Modifier
					.fillMaxWidth()
					.background(parseColor(tokens.color.surface))
					.padding(horizontal = layout.topBarPaddingInline.dp, vertical = layout.topBarPaddingBlock.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(layout.topBarGap.dp),
		) {
			Button(text = "← Назад", variant = VisualVariant.Ghost, onClick = onBack)
			Spacer(modifier = Modifier.weight(1f))
			topBarEnd()
		}

		// Content area
		Column(
			modifier =
				Modifier
					.fillMaxWidth()
					.padding(top = layout.contentPaddingBlockStart.dp, bottom = layout.contentPaddingBlockEnd.dp)
					.padding(horizontal = layout.contentPaddingInline.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			// Title
			Column(
				modifier = Modifier.fillMaxWidth().padding(vertical = layout.titlePaddingBlock.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				TextBlock(text = title, variant = TextBlockVariant.HeadlineLarge)
				Spacer(Modifier.height(layout.titleSubtitleGap.dp))
				BasicText(
					text = subtitle,
					style = TextStyle(
						fontSize = tokens.typography.bodyLarge.fontSize.sp,
						lineHeight = tokens.typography.bodyLarge.lineHeight.sp,
						letterSpacing = tokens.typography.bodyLarge.letterSpacing.sp,
						color = parseColor(tokens.color.textSecondary),
					),
				)
			}

			// Sections
			Column(
				modifier = Modifier.widthIn(max = layout.contentMaxWidth.dp),
				verticalArrangement = Arrangement.spacedBy(layout.sectionsGap.dp),
			) {
				content()
			}
		}
	}
}
