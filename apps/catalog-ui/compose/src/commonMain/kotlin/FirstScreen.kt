import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.CatalogLayoutResolver
import com.uikit.compose.components.composites.button.Button
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.foundation.LayoutDirection
import com.uikit.foundation.ThemeMode
import com.uikit.foundation.VisualVariant
import com.uikit.tokens.DesignTokens

@Composable
internal fun FirstScreen(
	tokens: DesignTokens,
	currentDir: LayoutDirection,
	currentMode: ThemeMode,
	onDirChange: (LayoutDirection) -> Unit,
	onThemeChange: (String) -> Unit,
	onNavigate: (String) -> Unit,
) {
	val layout = remember(tokens) { CatalogLayoutResolver.resolve(tokens) }

	Box(modifier = Modifier.fillMaxSize()) {
		Row(
			modifier = Modifier.align(Alignment.TopEnd).padding(layout.firstScreenControlsPadding.dp),
			horizontalArrangement = Arrangement.spacedBy(layout.firstScreenControlsGap.dp),
		) {
			DirSwitcherControl(currentDir, onDirChange)
			ThemeSwitcherControl(currentMode, onThemeChange)
		}

		Column(
			modifier = Modifier.fillMaxSize().padding(layout.contentPaddingBlockStart.dp),
			verticalArrangement = Arrangement.spacedBy(layout.firstScreenGap.dp, Alignment.CenterVertically),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			TextBlock(text = "UIKit Catalog", variant = TextBlockVariant.HeadlineLarge)
			Button(text = "Foundation Tokens", onClick = { onNavigate("/foundation") })
			Button(text = "Components", variant = VisualVariant.Soft, onClick = { onNavigate("/components") })
		}
	}
}
