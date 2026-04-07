import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.tokens.DesignTokens

@Composable
internal fun TextShowcase(tokens: DesignTokens) {
	ShowcaseSection("Варианты текста (Text)", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
			TextBlock(text = "Headline Large — Заголовок", variant = TextBlockVariant.HeadlineLarge)
			TextBlock(text = "Headline Medium — Подзаголовок", variant = TextBlockVariant.HeadlineMedium)
			TextBlock(text = "Title Large — Секция", variant = TextBlockVariant.TitleLarge)
			TextBlock(
				text = "Body Large — Основной текст для чтения. The quick brown fox jumps over the lazy dog. مرحبا بالعالم. 你好世界",
				variant = TextBlockVariant.BodyLarge,
			)
			TextBlock(text = "Label Medium — Подпись к элементам", variant = TextBlockVariant.LabelMedium)
		}
	}
}
