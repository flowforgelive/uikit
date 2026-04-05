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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.text.TextStyle
import com.uikit.compose.components.composites.segmentedcontrol.SegmentedControl
import com.uikit.compose.components.primitives.surface.Surface
import com.uikit.compose.theme.parseColor
import com.uikit.components.primitives.surface.SurfaceLevel
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
									BasicText(
										text = "Уровень ${level.ordinal}",
										style = TextStyle(
											fontSize = tokens.typography.labelMedium.fontSize.sp,
											fontWeight = FontWeight.SemiBold,
											lineHeight = tokens.typography.labelMedium.lineHeight.sp,
											letterSpacing = tokens.typography.labelMedium.letterSpacing.sp,
											color = parseColor(tokens.color.textPrimary),
										),
									)
									Spacer(Modifier.height(tokens.spacing.xs.dp))
									BasicText(
										text = variant.name.lowercase() + if (variant == VisualVariant.Ghost) " (hover)" else "",
										style = TextStyle(
											fontSize = tokens.typography.labelSmall.fontSize.sp,
											lineHeight = tokens.typography.labelSmall.lineHeight.sp,
											letterSpacing = tokens.typography.labelSmall.letterSpacing.sp,
											color = parseColor(tokens.color.textMuted),
										),
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
