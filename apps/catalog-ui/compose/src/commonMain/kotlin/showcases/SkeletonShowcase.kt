import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uikit.components.primitives.skeleton.SkeletonShape
import com.uikit.compose.components.primitives.skeleton.Skeleton
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.foundation.TextEmphasis
import com.uikit.tokens.DesignTokens

@Composable
internal fun SkeletonShowcase(tokens: DesignTokens) {
	ShowcaseSection("Скелетон (Skeleton)", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xl.dp)) {

			// Adaptive radius — Rectangle (capped by maxContainerRadius)
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				SubSectionTitle(text = "Адаптивное скругление Rectangle (с cap)", tokens = tokens)
				TextBlock(
					text = "Без явного cornerRadius → min(height × radiusFraction, maxContainerRadius). Переключите слайдер скругления.",
					variant = TextBlockVariant.BodySmall,
					emphasis = TextEmphasis.Muted,
				)
				Row(horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
					Skeleton(width = 80.0, height = 60.0)
					Skeleton(width = 200.0, height = 120.0)
					Skeleton(width = 300.0, height = 200.0)
				}
			}

			// Explicit radius — Rectangle (no cap)
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				SubSectionTitle(text = "Принудительное скругление (explicit, без cap)", tokens = tokens)
				TextBlock(
					text = "Явный cornerRadius игнорирует глобальный cap.",
					variant = TextBlockVariant.BodySmall,
					emphasis = TextEmphasis.Muted,
				)
				Row(horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
					Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
						Skeleton(width = 200.0, height = 120.0, cornerRadius = 30.0)
						TextBlock(text = "30dp", variant = TextBlockVariant.LabelSmall)
					}
					Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
						Skeleton(width = 200.0, height = 120.0, cornerRadius = 60.0)
						TextBlock(text = "60dp (pill)", variant = TextBlockVariant.LabelSmall)
					}
				}
			}

			// Rectangle — full width
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				SubSectionTitle(text = "Rectangle (full width)", tokens = tokens)
				Skeleton(height = 120.0)
			}

			// Rectangle — fixed width
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				SubSectionTitle(text = "Rectangle (Fixed size)", tokens = tokens)
				Skeleton(width = 200.0, height = 80.0, cornerRadius = 12.0)
			}

			// Circle
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				SubSectionTitle(text = "Circle", tokens = tokens)
				Row(horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
					Skeleton(shape = SkeletonShape.Circle, width = 40.0)
					Skeleton(shape = SkeletonShape.Circle, width = 56.0)
					Skeleton(shape = SkeletonShape.Circle, width = 72.0)
				}
			}

			// TextLine
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				SubSectionTitle(text = "TextLine", tokens = tokens)
				Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.sm.dp)) {
					Skeleton(shape = SkeletonShape.TextLine)
					Skeleton(
						shape = SkeletonShape.TextLine,
						modifier = Modifier.fillMaxWidth(0.75f),
					)
					Skeleton(
						shape = SkeletonShape.TextLine,
						modifier = Modifier.fillMaxWidth(0.5f),
					)
				}
			}

			// Card-like composition
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				SubSectionTitle(text = "Card placeholder", tokens = tokens)
				Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.sm.dp)) {
					Skeleton(height = 160.0, cornerRadius = 12.0)
					Row(horizontalArrangement = Arrangement.spacedBy(tokens.spacing.sm.dp)) {
						Skeleton(shape = SkeletonShape.Circle, width = 40.0)
						Column(
							verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp),
							modifier = Modifier.weight(1f),
						) {
							Skeleton(shape = SkeletonShape.TextLine)
							Skeleton(
								shape = SkeletonShape.TextLine,
								modifier = Modifier.fillMaxWidth(0.6f),
							)
						}
					}
				}
			}

			// Static (no animation)
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				SubSectionTitle(text = "Без анимации", tokens = tokens)
				Skeleton(height = 48.0, animate = false)
			}
		}
	}
}
