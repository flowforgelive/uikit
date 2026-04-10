import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.uikit.compose.components.primitives.glass.GlassSurface
import com.uikit.compose.components.primitives.surface.Surface
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.components.primitives.surface.SurfaceLevel
import com.uikit.components.primitives.surface.SurfaceShape
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.foundation.GlassVariant
import com.uikit.foundation.TextEmphasis
import com.uikit.tokens.DesignTokens

@Composable
internal fun GlassSurfaceShowcase(tokens: DesignTokens) {
	ShowcaseSection("Стеклянная поверхность (GlassSurface)", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xl.dp)) {
			SubSectionTitle(text = "Варианты", tokens = tokens)
			Row(
				horizontalArrangement = Arrangement.spacedBy(tokens.spacing.lg.dp),
			) {
				GlassVariant.entries.forEach { variant ->
					Box(
						modifier = Modifier
							.width(280.dp)
							.height(180.dp)
							.clip(RoundedCornerShape(tokens.radius.lg.dp))
							.background(
								Brush.linearGradient(
									colors = listOf(
										Color(0xFF4A90D9),
										Color(0xFFAA55CC),
									),
								),
							),
					) {
						Column(modifier = Modifier.padding(tokens.spacing.md.dp)) {
							TextBlock(
								text = "Контент за стеклом",
								variant = TextBlockVariant.BodyMedium,
							)
						}
						Box(
							modifier = Modifier
								.align(Alignment.BottomCenter)
								.padding(tokens.spacing.lg.dp)
								.fillMaxWidth(),
						) {
							GlassSurface(
								variant = variant,
								shape = SurfaceShape.Lg,
							) {
								Column(modifier = Modifier.padding(tokens.spacing.md.dp)) {
									TextBlock(
										text = variant.name,
										variant = TextBlockVariant.TitleSmall,
										emphasis = TextEmphasis.Primary,
									)
									TextBlock(
										text = if (variant == GlassVariant.Regular) "Tinted alpha" else "Subtle alpha",
										variant = TextBlockVariant.BodySmall,
									)
								}
							}
						}
					}
				}
			}

			SubSectionTitle(text = "Поверхность + текст адаптация", tokens = tokens)
			Row(
				horizontalArrangement = Arrangement.spacedBy(tokens.spacing.lg.dp),
			) {
				listOf(SurfaceLevel.Level0, SurfaceLevel.Level2, SurfaceLevel.Level4).forEach { level ->
					Surface(
						level = level,
						shape = SurfaceShape.Lg,
						modifier = Modifier.width(200.dp),
					) {
						Column(modifier = Modifier.padding(tokens.spacing.lg.dp)) {
							TextBlock(
								text = "Surface ${level.ordinal}",
								variant = TextBlockVariant.TitleSmall,
								emphasis = TextEmphasis.Primary,
							)
							TextBlock(
								text = "Основной текст",
								variant = TextBlockVariant.BodyMedium,
								emphasis = TextEmphasis.Primary,
							)
							TextBlock(
								text = "Второстепенный текст",
								variant = TextBlockVariant.BodySmall,
								emphasis = TextEmphasis.Secondary,
							)
							TextBlock(
								text = "Приглушённый текст",
								variant = TextBlockVariant.LabelSmall,
								emphasis = TextEmphasis.Muted,
							)
						}
					}
				}
			}
		}
	}
}
