import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.CatalogLayoutResolver
import com.uikit.compose.components.blocks.panel.Panel
import com.uikit.compose.components.composites.button.Button
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.components.blocks.panel.PanelCollapsible
import com.uikit.components.blocks.panel.PanelSide
import com.uikit.components.blocks.panel.PanelVariant
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.compose.theme.parseColor
import com.uikit.foundation.TextEmphasis
import com.uikit.foundation.VisualVariant
import com.uikit.tokens.DesignTokens

@Composable
internal fun CatalogPage(
	title: String,
	subtitle: String,
	tokens: DesignTokens,
	onBack: () -> Unit,
	panelVariant: PanelVariant = PanelVariant.Pinned,
	panelSide: PanelSide = PanelSide.Left,
	panelTokens: DesignTokens = tokens,
	panelContent: (@Composable () -> Unit)? = null,
	content: @Composable () -> Unit,
) {
	val layout = remember(tokens) { CatalogLayoutResolver.resolve(tokens) }
	val isHorizontal = panelSide == PanelSide.Top || panelSide == PanelSide.Bottom

	val panelBlock: @Composable () -> Unit = {
		if (panelContent != null) {
			CompositionLocalProvider(LocalDesignTokens provides panelTokens) {
				Panel(
					variant = panelVariant,
					side = panelSide,
					collapsible = PanelCollapsible.None,
					isOpen = true,
					width = 280.0,
					height = 220.0,
				) {
					Column(
						modifier = Modifier
							.then(if (isHorizontal) Modifier.fillMaxWidth() else Modifier.fillMaxHeight())
							.padding(panelTokens.spacing.lg.dp),
						verticalArrangement = Arrangement.spacedBy(panelTokens.spacing.xl.dp),
					) {
						panelContent()
					}
				}
			}
		}
	}

	@Composable
	fun ScrollableContent() {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState()),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.background(parseColor(tokens.color.surface))
					.padding(horizontal = layout.topBarPaddingInline.dp, vertical = layout.topBarPaddingBlock.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				Button(text = "← Назад", variant = VisualVariant.Ghost, onClick = onBack)
			}

			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = layout.contentPaddingBlockStart.dp, bottom = layout.contentPaddingBlockEnd.dp)
					.padding(horizontal = layout.contentPaddingInline.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				Column(
					modifier = Modifier.fillMaxWidth().padding(vertical = layout.titlePaddingBlock.dp),
					horizontalAlignment = Alignment.CenterHorizontally,
				) {
					TextBlock(text = title, variant = TextBlockVariant.HeadlineLarge)
					Spacer(Modifier.height(layout.titleSubtitleGap.dp))
					TextBlock(
						text = subtitle,
						variant = TextBlockVariant.BodyLarge,
						emphasis = TextEmphasis.Secondary,
					)
				}

				Column(
					modifier = Modifier.widthIn(max = layout.contentMaxWidth.dp),
					verticalArrangement = Arrangement.spacedBy(layout.sectionsGap.dp),
				) {
					content()
				}
			}
		}
	}

	if (isHorizontal) {
		Column(modifier = Modifier.fillMaxSize()) {
			if (panelSide == PanelSide.Top) { panelBlock() }
			Box(modifier = Modifier.weight(1f)) { ScrollableContent() }
			if (panelSide == PanelSide.Bottom) { panelBlock() }
		}
	} else {
		Row(modifier = Modifier.fillMaxSize()) {
			if (panelSide == PanelSide.Left) { panelBlock() }
			Box(modifier = Modifier.weight(1f)) { ScrollableContent() }
			if (panelSide == PanelSide.Right) { panelBlock() }
		}
	}
}
