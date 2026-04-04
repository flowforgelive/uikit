import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.text.TextStyle
import com.uikit.compose.components.atoms.segmentedcontrol.SegmentedControl
import com.uikit.compose.theme.parseColor
import com.uikit.foundation.ComponentSize
import com.uikit.foundation.IconPosition
import com.uikit.foundation.VisualVariant
import com.uikit.tokens.DesignTokens
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon

@Composable
internal fun SegmentedControlShowcase(tokens: DesignTokens, globalSize: ComponentSize) {
	var selected by remember { mutableStateOf("a") }
	val selectedSize = globalSize

	ShowcaseSection("Сегментированный переключатель (Segmented Control)", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.lg.dp)) {

			Column {
				BasicText(
					text = "3 опции",
					style = TextStyle(
						fontSize = tokens.typography.bodySmall.fontSize.sp,
						lineHeight = tokens.typography.bodySmall.lineHeight.sp,
						letterSpacing = tokens.typography.bodySmall.letterSpacing.sp,
						color = parseColor(tokens.color.textMuted),
					),
				)
				Spacer(Modifier.height(tokens.spacing.xs.dp))
				SegmentedControl(
					options = listOf("a" to "First", "b" to "Second", "c" to "Third"),
					selectedId = selected,
					onSelectionChange = { selected = it },
					size = selectedSize,
					modifier = Modifier.fillMaxWidth(),
				)
			}
			Column {
				BasicText(
					text = "2 опции",
					style = TextStyle(
						fontSize = tokens.typography.bodySmall.fontSize.sp,
						lineHeight = tokens.typography.bodySmall.lineHeight.sp,
						letterSpacing = tokens.typography.bodySmall.letterSpacing.sp,
						color = parseColor(tokens.color.textMuted),
					),
				)
				Spacer(Modifier.height(tokens.spacing.xs.dp))
				SegmentedControl(
					options = listOf("on" to "Вкл", "off" to "Выкл"),
					selectedId = "on",
					onSelectionChange = {},
					size = selectedSize,
					modifier = Modifier.fillMaxWidth(),
				)
			}

			// Variant showcase
			Column {
				BasicText(
					text = "Варианты (Variants)",
					style = TextStyle(
						fontSize = tokens.typography.bodySmall.fontSize.sp,
						lineHeight = tokens.typography.bodySmall.lineHeight.sp,
						letterSpacing = tokens.typography.bodySmall.letterSpacing.sp,
						color = parseColor(tokens.color.textMuted),
					),
				)
				Spacer(Modifier.height(tokens.spacing.xs.dp))
				Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.sm.dp)) {
					listOf(VisualVariant.Surface, VisualVariant.Soft, VisualVariant.Outline, VisualVariant.Solid, VisualVariant.Ghost).forEach { variant ->
						Row(
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
						) {
							BasicText(
								text = variant.name.lowercase(),
								style = TextStyle(
									fontSize = tokens.typography.labelMedium.fontSize.sp,
									lineHeight = tokens.typography.labelMedium.lineHeight.sp,
									letterSpacing = tokens.typography.labelMedium.letterSpacing.sp,
									color = parseColor(tokens.color.textMuted),
								),
								modifier = Modifier.width(64.dp),
							)
							SegmentedControl(
								options = listOf("a" to "First", "b" to "Second", "c" to "Third"),
								selectedId = selected,
								onSelectionChange = { selected = it },
								variant = variant,
								size = selectedSize,							modifier = Modifier.weight(1f),							)
						}
					}
				}
			}

			// Icon positions showcase
			Column {
				BasicText(
					text = "С иконками (Icons)",
					style = TextStyle(
						fontSize = tokens.typography.bodySmall.fontSize.sp,
						lineHeight = tokens.typography.bodySmall.lineHeight.sp,
						letterSpacing = tokens.typography.bodySmall.letterSpacing.sp,
						color = parseColor(tokens.color.textMuted),
					),
				)
				Spacer(Modifier.height(tokens.spacing.xs.dp))
				Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.sm.dp)) {
					val iconOptions = listOf("search" to "Search", "star" to "Star", "settings" to "Settings")
					val iconMap: Map<String, @Composable () -> Unit> = mapOf(
						"search" to { Icon(Icons.Filled.Search, contentDescription = null) },
						"star" to { Icon(Icons.Filled.Star, contentDescription = null) },
						"settings" to { Icon(Icons.Filled.Settings, contentDescription = null) },
					)
					listOf(
						"start" to IconPosition.Start,
						"end" to IconPosition.End,
						"top" to IconPosition.Top,
						"bottom" to IconPosition.Bottom,
					).forEach { (label, pos) ->
						Row(
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
						) {
							BasicText(
								text = label,
								style = TextStyle(
									fontSize = tokens.typography.labelMedium.fontSize.sp,
									lineHeight = tokens.typography.labelMedium.lineHeight.sp,
									letterSpacing = tokens.typography.labelMedium.letterSpacing.sp,
									color = parseColor(tokens.color.textMuted),
								),
								modifier = Modifier.width(64.dp),
							)
							SegmentedControl(
								options = iconOptions,
								selectedId = selected,
								onSelectionChange = { selected = it },
								iconPosition = pos,
								icons = iconMap,
								size = selectedSize,							modifier = Modifier.weight(1f),							)
						}
					}
				}
			}
		}
	}
}
