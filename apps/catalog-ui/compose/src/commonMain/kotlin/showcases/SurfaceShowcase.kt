import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uikit.compose.components.composites.segmentedcontrol.SegmentedControl
import com.uikit.compose.components.primitives.surface.Surface
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.components.primitives.surface.SurfaceLevel
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.foundation.TextEmphasis
import com.uikit.foundation.VisualVariant
import com.uikit.tokens.DesignTokens

@Composable
internal fun SurfaceShowcase(tokens: DesignTokens) {
	var surfaceMode by remember { mutableStateOf("default") }

	ShowcaseSection("Поверхность (Surface) — Вариант × Уровень", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xl.dp)) {
			SegmentedControl(
				options = listOf(
					"default" to "Обычный",
					"hoverable" to "По наведению",
					"clickable" to "По клику",
				),
				selectedId = surfaceMode,
				onSelectionChange = { surfaceMode = it },
				modifier = Modifier.fillMaxWidth(),
			)

			VisualVariant.entries.forEach { variant ->
				Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
					SubSectionTitle(text = variant.name, tokens = tokens)
					Row(
						horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
						modifier = Modifier.horizontalScroll(rememberScrollState()),
					) {
						SurfaceLevel.entries.forEach { level ->
							Surface(
								variant = variant,
								level = level,
								hoverable = surfaceMode == "hoverable",
								clickable = surfaceMode == "clickable",
								onClick = if (surfaceMode == "clickable") ({}) else null,
								modifier = Modifier.width(120.dp),
							) {
								Column(modifier = Modifier.padding(tokens.spacing.md.dp)) {
									TextBlock(
										text = "Уровень ${level.ordinal}",
										variant = TextBlockVariant.LabelMedium,
										emphasis = TextEmphasis.Primary,
									)
									Spacer(Modifier.height(tokens.spacing.xs.dp))
									TextBlock(
										text = variant.name.lowercase() + if (variant == VisualVariant.Ghost) " (hover)" else "",
										variant = TextBlockVariant.LabelSmall,
									)
								}
							}
						}
					}
				}
			}
		}
	}
}
