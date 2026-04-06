import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import catalog.CatalogLayoutResolver
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.foundation.LayoutDirection
import com.uikit.foundation.ThemeMode
import com.uikit.tokens.DesignTokens

@Composable
internal fun FoundationScreen(
	tokens: DesignTokens,
	currentMode: ThemeMode,
	currentDir: LayoutDirection,
	onDirChange: (LayoutDirection) -> Unit,
	onThemeChange: (String) -> Unit,
	onBack: () -> Unit,
) {
	CatalogPage(
		title = "Foundation Tokens",
		subtitle = "Типография, цвета, отступы, размеры, радиусы, анимации, брейкпоинты",
		tokens = tokens,
		onBack = onBack,
		panelContent = {
			// Direction
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				TextBlock(text = "Направление", variant = TextBlockVariant.LabelSmall)
				DirSwitcherControl(currentDir, onDirChange)
			}
			// Theme
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				TextBlock(text = "Тема", variant = TextBlockVariant.LabelSmall)
				ThemeSwitcherControl(currentMode, onThemeChange)
			}
		},
	) {
		TypographyShowcase(tokens)
		ColorsShowcase(tokens)
		SpacingShowcase(tokens)
		SizingShowcase(tokens)
		RadiusShowcase(tokens)
		MotionShowcase(tokens)
		BreakpointsShowcase(tokens)
	}
}
