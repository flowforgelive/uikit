import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.uikit.compose.components.composites.button.Button
import com.uikit.compose.components.primitives.image.Image
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.components.primitives.image.ImageFit
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.foundation.ComponentSize
import com.uikit.foundation.TextEmphasis
import com.uikit.foundation.VisualVariant
import com.uikit.tokens.DesignTokens
import catalog.CatalogOptions

// picsum.photos — random photos from Unsplash, /id/{N}/{W}/{H}.jpg for stable results
private const val PHOTO_1 = "https://picsum.photos/id/10/400/300.jpg"
private const val PHOTO_2 = "https://picsum.photos/id/1015/400/300.jpg"
private const val PHOTO_SQ = "https://picsum.photos/id/237/200/200.jpg"

// Large images for visible loading demo
private const val PHOTO_LARGE_1 = "https://picsum.photos/id/29/1600/1200.jpg"
private const val PHOTO_LARGE_2 = "https://picsum.photos/id/47/1600/1200.jpg"
private const val PHOTO_LARGE_3 = "https://picsum.photos/id/76/1600/1200.jpg"

@Composable
internal fun ImageShowcase(tokens: DesignTokens, globalSize: ComponentSize) {
	val s = remember(globalSize) { CatalogOptions.scaleFactor(globalSize.name) }

	ShowcaseSection("Изображение (Image)", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xl.dp)) {

			TextBlock(
				text = "Image — загрузка изображений по URL с размерами, скруглением, subtle border, skeleton-загрузкой и fallback.",
				variant = TextBlockVariant.BodySmall,
				emphasis = TextEmphasis.Muted,
			)

			// Adaptive corner radius (follows global radiusFraction, capped by maxContainerRadius)
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				SubSectionTitle(text = "Адаптивное скругление (глобальное с cap)", tokens = tokens)
				TextBlock(
					text = "Без явного cornerRadius → min(min(w,h) × radiusFraction, maxContainerRadius). Переключите слайдер скругления.",
					variant = TextBlockVariant.BodySmall,
					emphasis = TextEmphasis.Muted,
				)
				Row(horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
					Image(src = PHOTO_SQ, width = 80.0 * s, height = 80.0 * s, showBorder = true)
					Image(src = PHOTO_1, width = 120.0 * s, height = 80.0 * s, showBorder = true)
					Image(src = PHOTO_2, width = 200.0 * s, height = 140.0 * s, showBorder = true)
					Image(src = PHOTO_SQ, width = 120.0 * s, height = 120.0 * s, showBorder = true)
				}
			}

			// Explicit shapes: circle (avatar), pill (banner), sharp
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				SubSectionTitle(text = "Принудительная форма (explicit)", tokens = tokens)
				TextBlock(
					text = "Явное cornerRadius игнорирует глобальный cap. Circle = min(w,h)/2, pill = height/2, sharp = 0.",
					variant = TextBlockVariant.BodySmall,
					emphasis = TextEmphasis.Muted,
				)
				Row(horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
					Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
						Image(src = PHOTO_SQ, width = 80.0 * s, height = 80.0 * s, cornerRadius = 40.0 * s, showBorder = true)
						TextBlock(text = "Circle (avatar)", variant = TextBlockVariant.LabelSmall)
					}
					Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
						Image(src = PHOTO_1, width = 160.0 * s, height = 60.0 * s, cornerRadius = 30.0 * s, showBorder = true)
						TextBlock(text = "Pill (banner)", variant = TextBlockVariant.LabelSmall)
					}
					Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
						Image(src = PHOTO_2, width = 120.0 * s, height = 80.0 * s, cornerRadius = 0.0, showBorder = true)
						TextBlock(text = "Sharp (0)", variant = TextBlockVariant.LabelSmall)
					}
				}
			}

			// Adaptive cap vs Explicit — same size, different behavior
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				SubSectionTitle(text = "Adaptive cap vs Explicit (200×140)", tokens = tokens)
				TextBlock(
					text = "Оба изображения 200×140. Слева — adaptive (cap ограничивает). Справа — explicit cornerRadius=70 (без cap).",
					variant = TextBlockVariant.BodySmall,
					emphasis = TextEmphasis.Muted,
				)
				Row(horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
					Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
						Image(src = PHOTO_1, width = 200.0 * s, height = 140.0 * s, showBorder = true)
						TextBlock(text = "Adaptive (с cap)", variant = TextBlockVariant.LabelSmall, emphasis = TextEmphasis.Muted)
					}
					Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
						Image(src = PHOTO_1, width = 200.0 * s, height = 140.0 * s, cornerRadius = 70.0 * s, showBorder = true)
						TextBlock(text = "Explicit 70dp (без cap)", variant = TextBlockVariant.LabelSmall, emphasis = TextEmphasis.Muted)
					}
				}
			}

			// Border
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				SubSectionTitle(text = "Subtle border (showBorder)", tokens = tokens)
				Row(horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
					Image(src = PHOTO_SQ, width = 80.0 * s, height = 80.0 * s, showBorder = true)
					Image(src = PHOTO_SQ, width = 80.0 * s, height = 80.0 * s, cornerRadius = 40.0 * s, showBorder = true)
					Image(src = PHOTO_2, width = 120.0 * s, height = 80.0 * s)
				}
			}

			// Object-fit variants
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				SubSectionTitle(text = "Варианты objectFit", tokens = tokens)
				Row(horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
					ImageFit.entries.forEach { fit ->
						Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp)) {
							Image(
								src = PHOTO_1,
								width = 80.0 * s,
								height = 80.0 * s,
								objectFit = fit,
								showBorder = true,
							)
							TextBlock(text = fit.name, variant = TextBlockVariant.LabelSmall)
						}
					}
				}
			}

			// Skeleton loading → image transition
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				SubSectionTitle(text = "Skeleton → загрузка изображения", tokens = tokens)
				TextBlock(
					text = "При загрузке отображается shimmer-skeleton. Нажмите кнопку для перезагрузки (сброс кэша).",
					variant = TextBlockVariant.BodySmall,
					emphasis = TextEmphasis.Muted,
				)
				var reloadKey by remember { mutableStateOf(0) }
				Button(
					text = "⟳ Перезагрузить",
					variant = VisualVariant.Outline,
					size = ComponentSize.Sm,
					onClick = { reloadKey++ },
				)
				// key forces re-compose → re-fetch images
				androidx.compose.runtime.key(reloadKey) {
					Row(horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
						Image(
							src = "$PHOTO_LARGE_1?v=$reloadKey",
							width = 200.0 * s,
							height = 140.0 * s,
							cornerRadius = 12.0 * s,
							showBorder = true,
						)
						Image(
							src = "$PHOTO_LARGE_2?v=$reloadKey",
							width = 140.0 * s,
							height = 140.0 * s,
							cornerRadius = 70.0 * s,
							showBorder = true,
						)
						Image(
							src = "$PHOTO_LARGE_3?v=$reloadKey",
							width = 180.0 * s,
							height = 120.0 * s,
							cornerRadius = 8.0 * s,
							showBorder = true,
						)
					}
				}
			}

			// Fallback on error
			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				SubSectionTitle(text = "Fallback при ошибке загрузки", tokens = tokens)
				Row(horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
					Image(
						src = "https://invalid.url/broken.jpg",
						fallback = PHOTO_SQ,
						width = 100.0 * s,
						height = 100.0 * s,
						showBorder = true,
					)
				}
			}
		}
	}
}
