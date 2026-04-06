import androidx.compose.runtime.Composable
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.tokens.DesignTokens

@Composable
internal fun SubSectionTitle(
	text: String,
	tokens: DesignTokens,
) {
	TextBlock(
		text = text.replaceFirstChar { it.uppercase() },
		variant = TextBlockVariant.TitleLarge,
	)
}
