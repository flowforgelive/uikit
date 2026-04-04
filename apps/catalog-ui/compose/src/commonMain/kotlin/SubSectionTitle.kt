import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.uikit.compose.theme.parseColor
import com.uikit.tokens.DesignTokens

@Composable
internal fun SubSectionTitle(
	text: String,
	tokens: DesignTokens,
) {
	BasicText(
		text = text.replaceFirstChar { it.uppercase() },
		style = TextStyle(
			fontSize = tokens.typography.titleLarge.fontSize.sp,
			fontWeight = FontWeight(600),
			lineHeight = tokens.typography.titleLarge.lineHeight.sp,
			letterSpacing = tokens.typography.titleLarge.letterSpacing.sp,
			color = parseColor(tokens.color.textPrimary),
		),
		maxLines = 1,
		overflow = TextOverflow.Ellipsis,
	)
}
