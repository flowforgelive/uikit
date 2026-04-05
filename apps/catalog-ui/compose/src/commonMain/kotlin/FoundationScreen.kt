import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import catalog.CatalogLayoutResolver
import com.uikit.compose.theme.parseColor
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
				BasicText(
					text = "Направление",
					style = TextStyle(
						fontSize = tokens.typography.labelSmall.fontSize.sp,
						color = parseColor(tokens.color.textMuted),
					),
				)
				DirSwitcherControl(currentDir, onDirChange)
			}
			// Theme
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
				BasicText(
					text = "Тема",
					style = TextStyle(
						fontSize = tokens.typography.labelSmall.fontSize.sp,
						color = parseColor(tokens.color.textMuted),
					),
				)
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
