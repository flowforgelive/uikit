import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import catalog.CatalogLayoutResolver
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
	val layout = remember(tokens) { CatalogLayoutResolver.resolve(tokens) }

	CatalogPage(
		title = "Foundation Tokens",
		subtitle = "Типография, цвета, отступы, размеры, радиусы, анимации, брейкпоинты",
		tokens = tokens,
		topBarEnd = {
			Row(horizontalArrangement = Arrangement.spacedBy(layout.topBarGap.dp)) {
				DirSwitcherControl(currentDir, onDirChange)
				ThemeSwitcherControl(currentMode, onThemeChange)
			}
		},
		onBack = onBack,
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
