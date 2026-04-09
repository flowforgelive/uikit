import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.uikit.compose.components.blocks.panel.Panel
import com.uikit.compose.components.composites.segmentedcontrol.SegmentedControl
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.components.blocks.panel.PanelCollapsible
import com.uikit.components.blocks.panel.PanelSide
import com.uikit.components.blocks.panel.PanelVariant
import com.uikit.components.primitives.surface.SurfaceLevel
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.tokens.DesignTokens

private val SHOWCASE_PANEL_VARIANT_OPTIONS = listOf(
	"pinned" to "Pinned",
	"inset" to "Inset",
)

private val SHOWCASE_PANEL_SIDE_OPTIONS = listOf(
	"left" to "Left",
	"right" to "Right",
	"top" to "Top",
	"bottom" to "Bottom",
)

private val PANEL_COLLAPSIBLE_OPTIONS = listOf(
	"offcanvas" to "Offcanvas",
	"icon" to "Icon",
	"none" to "None",
)

private val PANEL_LEVEL_OPTIONS = listOf(
	"0" to "Level 0",
	"1" to "Level 1",
	"2" to "Level 2",
	"3" to "Level 3",
)

private fun panelSideFromId(id: String): PanelSide = when (id) {
	"right" -> PanelSide.Right
	"top" -> PanelSide.Top
	"bottom" -> PanelSide.Bottom
	else -> PanelSide.Left
}

private fun panelCollapsibleFromId(id: String): PanelCollapsible = when (id) {
	"icon" -> PanelCollapsible.Icon
	"none" -> PanelCollapsible.None
	else -> PanelCollapsible.Offcanvas
}

private fun surfaceLevelFromId(id: String): SurfaceLevel = when (id) {
	"0" -> SurfaceLevel.Level0
	"2" -> SurfaceLevel.Level2
	"3" -> SurfaceLevel.Level3
	else -> SurfaceLevel.Level1
}

@Composable
internal fun PanelShowcase(tokens: DesignTokens) {
	var variantId by remember { mutableStateOf("pinned") }
	var sideId by remember { mutableStateOf("left") }
	var collapsibleId by remember { mutableStateOf("offcanvas") }
	var levelId by remember { mutableStateOf("1") }
	var isOpen by remember { mutableStateOf(true) }

	val variant = if (variantId == "inset") PanelVariant.Inset else PanelVariant.Pinned
	val side = panelSideFromId(sideId)
	val collapsible = panelCollapsibleFromId(collapsibleId)
	val surfaceLevel = surfaceLevelFromId(levelId)

	ShowcaseSection("Панель (Panel)", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xl.dp)) {

			Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
				Row(
					horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
					modifier = Modifier.fillMaxWidth(),
				) {
					Column(
						verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp),
						modifier = Modifier.weight(1f),
					) {
						TextBlock(text = "Вариант:", variant = TextBlockVariant.LabelMedium)
						SegmentedControl(
							options = SHOWCASE_PANEL_VARIANT_OPTIONS,
							selectedId = variantId,
							onSelectionChange = { variantId = it },
						)
					}
					Column(
						verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp),
						modifier = Modifier.weight(1f),
					) {
						TextBlock(text = "Сторона:", variant = TextBlockVariant.LabelMedium)
						SegmentedControl(
							options = SHOWCASE_PANEL_SIDE_OPTIONS,
							selectedId = sideId,
							onSelectionChange = { sideId = it },
						)
					}
				}
				Row(
					horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
					modifier = Modifier.fillMaxWidth(),
				) {
					Column(
						verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp),
						modifier = Modifier.weight(1f),
					) {
						TextBlock(text = "Сворачивание:", variant = TextBlockVariant.LabelMedium)
						SegmentedControl(
							options = PANEL_COLLAPSIBLE_OPTIONS,
							selectedId = collapsibleId,
							onSelectionChange = { collapsibleId = it },
						)
					}
					Column(
						verticalArrangement = Arrangement.spacedBy(tokens.spacing.xs.dp),
						modifier = Modifier.weight(1f),
					) {
						TextBlock(text = "Уровень:", variant = TextBlockVariant.LabelMedium)
						SegmentedControl(
							options = PANEL_LEVEL_OPTIONS,
							selectedId = levelId,
							onSelectionChange = { levelId = it },
						)
					}
				}
			}

			SubSectionTitle(text = "Интерактивный пример", tokens = tokens)
			Panel(
				variant = variant,
				side = side,
				collapsible = collapsible,
				isOpen = isOpen,
				surfaceLevel = surfaceLevel,
				width = 220.0,
				height = 160.0,
				onToggle = { isOpen = !isOpen },
			) {
				Column(modifier = Modifier.padding(tokens.spacing.md.dp)) {
					TextBlock(
						text = "${variant.name} / ${side.name}",
						variant = TextBlockVariant.LabelMedium,
					)
					TextBlock(
						text = "Collapsible: ${collapsible.name}",
						variant = TextBlockVariant.LabelSmall,
					)
					TextBlock(
						text = "Level: $levelId",
						variant = TextBlockVariant.LabelSmall,
					)
				}
			}

			SubSectionTitle(text = "Все варианты × стороны", tokens = tokens)
			PanelVariant.entries.forEach { v ->
				Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
					TextBlock(
						text = v.name,
						variant = TextBlockVariant.LabelMedium,
					)
					Row(
						horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
						modifier = Modifier.horizontalScroll(rememberScrollState()),
					) {
						PanelSide.entries.forEach { s ->
							Panel(
								variant = v,
								side = s,
								collapsible = PanelCollapsible.None,
								isOpen = true,
								width = 140.0,
								height = 100.0,
								surfaceLevel = SurfaceLevel.Level1,
							) {
								Column(modifier = Modifier.padding(tokens.spacing.sm.dp)) {
									TextBlock(
										text = s.name,
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
