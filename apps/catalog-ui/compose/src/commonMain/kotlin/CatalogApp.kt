import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uikit.components.atoms.button.ButtonSize
import com.uikit.components.atoms.text.TextBlockVariant
import com.uikit.components.atoms.surface.SurfaceLevel
import com.uikit.compose.components.atoms.button.Button
import com.uikit.compose.components.atoms.iconbutton.IconButton
import com.uikit.compose.components.atoms.segmentedcontrol.SegmentedControl
import com.uikit.compose.components.atoms.text.TextBlock
import com.uikit.compose.components.atoms.surface.Surface
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.UIKitTheme
import com.uikit.compose.theme.parseColor
import com.uikit.foundation.ColorIntent
import com.uikit.foundation.ComponentSize
import com.uikit.foundation.IconPosition
import com.uikit.foundation.LayoutDirection
import com.uikit.foundation.VisualVariant
import com.uikit.foundation.InMemoryThemeProvider
import com.uikit.foundation.ThemeMode
import com.uikit.tokens.DesignTokens
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon

@Composable
fun CatalogApp() {
	var currentScreen by remember { mutableStateOf("first") }
	val themeProvider = remember { InMemoryThemeProvider(ThemeMode.System) }
	var currentDir by remember { mutableStateOf(LayoutDirection.Ltr) }
	val scope = rememberCoroutineScope()

	val onAction: (String) -> Unit = { route ->
		currentScreen = route.removePrefix("/")
	}

	UIKitTheme(themeProvider = themeProvider, layoutDirection = currentDir) {
		val currentTokens = LocalDesignTokens.current
		val currentMode by themeProvider
			.observeThemeMode()
			.collectAsState(initial = ThemeMode.System)
		val focusManager = LocalFocusManager.current

		Box(
			modifier =
				Modifier
					.fillMaxSize()
					.background(parseColor(currentTokens.color.surface))
					.pointerInput(Unit) {
						detectTapGestures { focusManager.clearFocus() }
					},
		) {
			when (currentScreen) {
				"first" -> {
					// Theme & dir switchers — top right
					Row(
						modifier =
							Modifier
								.align(Alignment.TopEnd)
								.padding(16.dp),
						horizontalArrangement = Arrangement.spacedBy(8.dp),
					) {
						DirSwitcherControl(currentDir) { currentDir = it }
						ThemeSwitcherControl(currentMode) { id ->
							scope.launch {
								themeProvider.setTheme(
									when (id) {
										"dark" -> ThemeMode.Dark
										"light" -> ThemeMode.Light
										else -> ThemeMode.System
									},
								)
							}
						}
					}

					Column(
						modifier = Modifier.fillMaxSize().padding(32.dp),
						verticalArrangement = Arrangement.Center,
						horizontalAlignment = Alignment.CenterHorizontally,
					) {
						FirstScreen(onAction)
					}
				}

				"foundation" -> {
					FoundationScreen(
						tokens = currentTokens,
						currentMode = currentMode,
						currentDir = currentDir,
						onDirChange = { currentDir = it },
						onThemeChange = { id ->
							scope.launch {
								themeProvider.setTheme(
									when (id) {
										"dark" -> ThemeMode.Dark
										"light" -> ThemeMode.Light
										else -> ThemeMode.System
									},
								)
							}
						},
						onBack = { onAction("/first") },
					)
				}

				"components" -> {
					ComponentsScreen(
						tokens = currentTokens,
						currentMode = currentMode,
						currentDir = currentDir,
						onDirChange = { currentDir = it },
						onThemeChange = { id ->
							scope.launch {
								themeProvider.setTheme(
									when (id) {
										"dark" -> ThemeMode.Dark
										"light" -> ThemeMode.Light
										else -> ThemeMode.System
									},
								)
							}
						},
						onBack = { onAction("/first") },
					)
				}
			}
		}
	}
}

/* ─── Shared controls ────────────────────────────────────── */

@Composable
private fun ThemeSwitcherControl(
	currentMode: ThemeMode,
	onThemeChange: (String) -> Unit,
) {
	SegmentedControl(
		options = listOf("dark" to "Тёмная", "light" to "Светлая", "system" to "Система"),
		selectedId =
			when (currentMode) {
				ThemeMode.Dark -> "dark"
				ThemeMode.Light -> "light"
				ThemeMode.System -> "system"
			},
		onSelectionChange = onThemeChange,
		modifier = Modifier.width(240.dp),
	)
}

@Composable
private fun DirSwitcherControl(
	currentDir: LayoutDirection,
	onDirChange: (LayoutDirection) -> Unit,
) {
	SegmentedControl(
		options = listOf("ltr" to "LTR", "rtl" to "RTL"),
		selectedId = if (currentDir == LayoutDirection.Ltr) "ltr" else "rtl",
		onSelectionChange = { id ->
			onDirChange(if (id == "rtl") LayoutDirection.Rtl else LayoutDirection.Ltr)
		},
		modifier = Modifier.width(120.dp),
	)
}

/* ─── First screen ───────────────────────────────────────── */

@Composable
private fun FirstScreen(onAction: (String) -> Unit) {
	TextBlock(
		text = "UIKit Catalog",
		variant = TextBlockVariant.H1,
	)

	Spacer(Modifier.height(24.dp))

	Button(
		text = "Foundation Tokens",
		onClick = { onAction("/foundation") },
	)

	Spacer(Modifier.height(12.dp))

	Button(
		text = "Components",
		variant = VisualVariant.Soft,
		onClick = { onAction("/components") },
	)
}

/* ─── Section helper ─────────────────────────────────────── */

@Composable
private fun ShowcaseSection(
	title: String,
	tokens: DesignTokens,
	content: @Composable () -> Unit,
) {
	Column(modifier = Modifier.fillMaxWidth()) {
		Row(
			modifier = Modifier.fillMaxWidth().padding(bottom = tokens.spacing.lg.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(tokens.spacing.lg.dp),
		) {
			androidx.compose.material3.Text(
				text = title,
				fontSize = tokens.typography.title3.fontSize.sp,
				fontWeight = FontWeight(tokens.typography.title3.fontWeight),
				color = parseColor(tokens.color.textPrimary),
			)
			Box(modifier = Modifier.weight(1f).height(1.dp).background(parseColor(tokens.color.outlineVariant)))
		}
		content()
	}
}

/* ─── Foundation screen ─────────────────────────────────── */

@Composable
private fun FoundationScreen(
	tokens: DesignTokens,
	currentMode: ThemeMode,
	currentDir: LayoutDirection,
	onDirChange: (LayoutDirection) -> Unit,
	onThemeChange: (String) -> Unit,
	onBack: () -> Unit,
) {
	Column(
		modifier =
			Modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState()),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		// Top bar
		Row(
			modifier =
				Modifier
					.fillMaxWidth()
					.background(parseColor(tokens.color.surface))
					.padding(horizontal = tokens.spacing.xl.dp, vertical = tokens.spacing.md.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
		) {
			Button(text = "← Назад", variant = VisualVariant.Ghost, onClick = onBack)
			Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				DirSwitcherControl(currentDir, onDirChange)
				ThemeSwitcherControl(currentMode, onThemeChange)
			}
		}

		// Title
		Column(
			modifier = Modifier.fillMaxWidth().padding(vertical = tokens.spacing.lg.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			TextBlock(text = "Foundation Tokens", variant = TextBlockVariant.H1)
			Spacer(Modifier.height(tokens.spacing.sm.dp))
			androidx.compose.material3.Text(
				text = "Типография, цвета, отступы, размеры, радиусы, анимации, брейкпоинты",
				fontSize = tokens.typography.body.fontSize.sp,
				color = parseColor(tokens.color.textSecondary),
			)
		}

		// Sections
		Column(
			modifier =
				Modifier
					.widthIn(max = 960.dp)
					.padding(horizontal = tokens.spacing.xl.dp)
					.padding(bottom = tokens.spacing.xxxxl.dp),
			verticalArrangement = Arrangement.spacedBy(tokens.spacing.xxxl.dp),
		) {
			TypographyShowcase(tokens)
			ColorsShowcase(tokens)
			SpacingShowcase(tokens)
			SizingShowcase(tokens)
			RadiusShowcase(tokens)
			MotionShowcase(tokens)
			BreakpointsShowcase(tokens)
		}
	}
}

/* ─── Components screen ─────────────────────────────────── */

@Composable
private fun ComponentsScreen(
	tokens: DesignTokens,
	currentMode: ThemeMode,
	currentDir: LayoutDirection,
	onDirChange: (LayoutDirection) -> Unit,
	onThemeChange: (String) -> Unit,
	onBack: () -> Unit,
) {
	Column(
		modifier =
			Modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState()),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		// Top bar
		Row(
			modifier =
				Modifier
					.fillMaxWidth()
					.background(parseColor(tokens.color.surface))
					.padding(horizontal = tokens.spacing.xl.dp, vertical = tokens.spacing.md.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
		) {
			Button(text = "← Назад", variant = VisualVariant.Ghost, onClick = onBack)
			Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				DirSwitcherControl(currentDir, onDirChange)
				ThemeSwitcherControl(currentMode, onThemeChange)
			}
		}

		// Title
		Column(
			modifier = Modifier.fillMaxWidth().padding(vertical = tokens.spacing.lg.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			TextBlock(text = "Components", variant = TextBlockVariant.H1)
			Spacer(Modifier.height(tokens.spacing.sm.dp))
			androidx.compose.material3.Text(
				text = "Кнопки, иконки, поверхности, текст, контролы",
				fontSize = tokens.typography.body.fontSize.sp,
				color = parseColor(tokens.color.textSecondary),
			)
		}

		// Sections
		Column(
			modifier =
				Modifier
					.widthIn(max = 960.dp)
					.padding(horizontal = tokens.spacing.xl.dp)
					.padding(bottom = tokens.spacing.xxxxl.dp),
			verticalArrangement = Arrangement.spacedBy(tokens.spacing.xxxl.dp),
		) {
			ButtonShowcase(tokens)
			IconButtonShowcase(tokens)
			SurfaceShowcase(tokens)
			TextShowcase(tokens)
			SegmentedControlShowcase(tokens)
			HeightAlignmentShowcase(tokens)
		}
	}
}

/* ─── Typography ─────────────────────────────────────────── */

private data class TypoEntry(val key: String, val label: String)

private val TYPO_ENTRIES =
	listOf(
		TypoEntry("largeTitle", "Large Title"),
		TypoEntry("title1", "Title 1"),
		TypoEntry("title2", "Title 2"),
		TypoEntry("title3", "Title 3"),
		TypoEntry("headline", "Headline"),
		TypoEntry("body", "Body"),
		TypoEntry("callout", "Callout"),
		TypoEntry("subhead", "Subhead"),
		TypoEntry("footnote", "Footnote"),
		TypoEntry("caption1", "Caption 1"),
		TypoEntry("caption2", "Caption 2"),
	)

@Composable
private fun TypographyShowcase(tokens: DesignTokens) {
	ShowcaseSection("Typography", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
			TYPO_ENTRIES.forEach { entry ->
				val style = getTypoStyle(tokens, entry.key)
				Row(
					verticalAlignment = Alignment.Bottom,
					horizontalArrangement = Arrangement.spacedBy(tokens.spacing.lg.dp),
				) {
					androidx.compose.material3.Text(
						text = entry.label,
						fontSize = style.fontSize.sp,
						fontWeight = FontWeight(style.fontWeight),
						letterSpacing = style.letterSpacing.sp,
						color = parseColor(tokens.color.textPrimary),
					)
					androidx.compose.material3.Text(
						text = "${style.fontSize.toInt()}dp / ${style.fontWeight} / ${style.lineHeight.toInt()}dp",
						fontSize = tokens.typography.caption1.fontSize.sp,
						color = parseColor(tokens.color.textMuted),
					)
				}
			}
		}
	}
}

private fun getTypoStyle(
	tokens: DesignTokens,
	key: String,
) = when (key) {
	"largeTitle" -> tokens.typography.largeTitle
	"title1" -> tokens.typography.title1
	"title2" -> tokens.typography.title2
	"title3" -> tokens.typography.title3
	"headline" -> tokens.typography.headline
	"body" -> tokens.typography.body
	"callout" -> tokens.typography.callout
	"subhead" -> tokens.typography.subhead
	"footnote" -> tokens.typography.footnote
	"caption1" -> tokens.typography.caption1
	"caption2" -> tokens.typography.caption2
	else -> tokens.typography.body
}

/* ─── Colors ─────────────────────────────────────────────── */

private data class ColorEntry(val key: String, val label: String)

private val COLOR_ENTRIES =
	listOf(
		ColorEntry("primary", "Primary"),
		ColorEntry("primaryHover", "Primary Hover"),
		ColorEntry("secondary", "Secondary"),
		ColorEntry("danger", "Danger"),
		ColorEntry("dangerHover", "Danger Hover"),
		ColorEntry("dangerSoft", "Danger Soft"),
		ColorEntry("dangerSoftHover", "Danger Soft Hover"),
		ColorEntry("background", "Background"),
		ColorEntry("surface", "Surface"),
		ColorEntry("surfaceContainerLowest", "Container Lowest"),
		ColorEntry("surfaceContainerLow", "Container Low"),
		ColorEntry("surfaceContainer", "Surface Container"),
		ColorEntry("surfaceContainerHigh", "Container High"),
		ColorEntry("surfaceContainerHighest", "Container Highest"),
		ColorEntry("surfaceHover", "Surface Hover"),
		ColorEntry("onSurface", "On Surface"),
		ColorEntry("outline", "Outline"),
		ColorEntry("outlineVariant", "Outline Variant"),
		ColorEntry("textPrimary", "Text Primary"),
		ColorEntry("textSecondary", "Text Secondary"),
		ColorEntry("textMuted", "Text Muted"),
		ColorEntry("textOnPrimary", "Text on Primary"),
		ColorEntry("textOnDanger", "Text on Danger"),
		ColorEntry("textDisabled", "Text Disabled"),
		ColorEntry("surfaceDisabled", "Surface Disabled"),
		ColorEntry("borderDisabled", "Border Disabled"),
		ColorEntry("border", "Border"),
	)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColorsShowcase(tokens: DesignTokens) {
	ShowcaseSection("Colors", tokens) {
		FlowRow(
			horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
			verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
		) {
			COLOR_ENTRIES.forEach { entry ->
				val hex = getColorValue(tokens, entry.key)
				Column(modifier = Modifier.width(140.dp)) {
					Box(
						modifier =
							Modifier
								.fillMaxWidth()
								.aspectRatio(1f)
								.clip(RoundedCornerShape(tokens.radius.md.dp))
								.background(parseColor(hex))
								.border(1.dp, parseColor(tokens.color.border), RoundedCornerShape(tokens.radius.md.dp)),
					)
					Spacer(Modifier.height(tokens.spacing.xs.dp))
					androidx.compose.material3.Text(
						text = entry.label,
						fontSize = tokens.typography.caption1.fontSize.sp,
						fontWeight = FontWeight.SemiBold,
						color = parseColor(tokens.color.textPrimary),
					)
					androidx.compose.material3.Text(
						text = hex,
						fontSize = tokens.typography.caption2.fontSize.sp,
						color = parseColor(tokens.color.textMuted),
					)
				}
			}
		}
	}
}

private fun getColorValue(
	tokens: DesignTokens,
	key: String,
) = when (key) {
	"primary" -> tokens.color.primary
	"primaryHover" -> tokens.color.primaryHover
	"secondary" -> tokens.color.secondary
	"danger" -> tokens.color.danger
	"dangerHover" -> tokens.color.dangerHover
	"dangerSoft" -> tokens.color.dangerSoft
	"dangerSoftHover" -> tokens.color.dangerSoftHover
	"background" -> tokens.color.background
	"surface" -> tokens.color.surface
	"surfaceContainerLowest" -> tokens.color.surfaceContainerLowest
	"surfaceContainerLow" -> tokens.color.surfaceContainerLow
	"surfaceContainer" -> tokens.color.surfaceContainer
	"surfaceContainerHigh" -> tokens.color.surfaceContainerHigh
	"surfaceContainerHighest" -> tokens.color.surfaceContainerHighest
	"surfaceHover" -> tokens.color.surfaceHover
	"onSurface" -> tokens.color.onSurface
	"outline" -> tokens.color.outline
	"outlineVariant" -> tokens.color.outlineVariant
	"textPrimary" -> tokens.color.textPrimary
	"textSecondary" -> tokens.color.textSecondary
	"textMuted" -> tokens.color.textMuted
	"textOnPrimary" -> tokens.color.textOnPrimary
	"textOnDanger" -> tokens.color.textOnDanger
	"textDisabled" -> tokens.color.textDisabled
	"surfaceDisabled" -> tokens.color.surfaceDisabled
	"borderDisabled" -> tokens.color.borderDisabled
	"border" -> tokens.color.border
	else -> tokens.color.primary
}

/* ─── Spacing ────────────────────────────────────────────── */

private data class SpacingEntry(val label: String, val value: Double)

@Composable
private fun SpacingShowcase(tokens: DesignTokens) {
	val entries =
		listOf(
			SpacingEntry("xxs", tokens.spacing.xxs),
			SpacingEntry("xs", tokens.spacing.xs),
			SpacingEntry("sm", tokens.spacing.sm),
			SpacingEntry("md", tokens.spacing.md),
			SpacingEntry("lg", tokens.spacing.lg),
			SpacingEntry("xl", tokens.spacing.xl),
			SpacingEntry("xxl", tokens.spacing.xxl),
			SpacingEntry("xxxl", tokens.spacing.xxxl),
			SpacingEntry("xxxxl", tokens.spacing.xxxxl),
		)
	ShowcaseSection("Spacing", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.sm.dp)) {
			entries.forEach { entry ->
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
				) {
					androidx.compose.material3.Text(
						text = "${entry.label} (${entry.value.toInt()}dp)",
						fontSize = tokens.typography.caption1.fontSize.sp,
						color = parseColor(tokens.color.textMuted),
						modifier = Modifier.width(100.dp),
						textAlign = TextAlign.End,
					)
					Box(
						modifier =
							Modifier
								.width(entry.value.dp)
								.height(tokens.spacing.lg.dp)
								.clip(RoundedCornerShape(tokens.radius.xs.dp))
								.background(parseColor(tokens.color.primary)),
					)
				}
			}
		}
	}
}

/* ─── Sizing ─────────────────────────────────────────────── */

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SizingShowcase(tokens: DesignTokens) {
	val btnSizes =
		listOf(
			Triple("XS", tokens.sizing.controlXs, tokens.sizing.controlXs * 2),
			Triple("SM", tokens.sizing.controlSm, tokens.sizing.controlSm * 2),
			Triple("MD", tokens.sizing.controlMd, tokens.sizing.controlMd * 2),
			Triple("LG", tokens.sizing.controlLg, tokens.sizing.controlLg * 2),
			Triple("XL", tokens.sizing.controlXl, tokens.sizing.controlXl * 2),
		)
	val iconSizes =
		listOf(
			"XS" to tokens.sizing.iconXs,
			"SM" to tokens.sizing.iconSm,
			"MD" to tokens.sizing.iconMd,
			"LG" to tokens.sizing.iconLg,
			"XL" to tokens.sizing.iconXl,
		)

	ShowcaseSection("Sizing", tokens) {
		// Button Heights sub-header
		androidx.compose.material3.Text(
			text = "Button Heights",
			fontSize = tokens.typography.headline.fontSize.sp,
			fontWeight = FontWeight.SemiBold,
			color = parseColor(tokens.color.textPrimary),
		)
		Spacer(Modifier.height(tokens.spacing.sm.dp))
		FlowRow(
			horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
			verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
		) {
			btnSizes.forEach { (label, h, w) ->
				Column(horizontalAlignment = Alignment.CenterHorizontally) {
					Box(
						modifier =
							Modifier
								.width(w.dp)
								.height(h.dp)
								.clip(RoundedCornerShape(tokens.radius.md.dp))
								.background(parseColor(tokens.color.primary)),
						contentAlignment = Alignment.Center,
					) {
						androidx.compose.material3.Text(
							text = "${h.toInt()}dp",
							fontSize = tokens.typography.caption2.fontSize.sp,
							fontWeight = FontWeight.SemiBold,
							color = parseColor(tokens.color.textOnPrimary),
						)
					}
					Spacer(Modifier.height(tokens.spacing.xs.dp))
					androidx.compose.material3.Text(
						text = label,
						fontSize = tokens.typography.caption1.fontSize.sp,
						color = parseColor(tokens.color.textMuted),
					)
				}
			}
		}

		Spacer(Modifier.height(tokens.spacing.xl.dp))

		// Icon Sizes sub-header
		androidx.compose.material3.Text(
			text = "Icon Sizes",
			fontSize = tokens.typography.headline.fontSize.sp,
			fontWeight = FontWeight.SemiBold,
			color = parseColor(tokens.color.textPrimary),
		)
		Spacer(Modifier.height(tokens.spacing.sm.dp))
		FlowRow(
			horizontalArrangement = Arrangement.spacedBy(tokens.spacing.lg.dp),
			verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
		) {
			iconSizes.forEach { (label, size) ->
				Column(horizontalAlignment = Alignment.CenterHorizontally) {
					Box(
						modifier =
							Modifier
								.size(size.dp)
								.clip(RoundedCornerShape(tokens.radius.sm.dp))
								.background(parseColor(tokens.color.primary)),
					)
					Spacer(Modifier.height(tokens.spacing.xs.dp))
					androidx.compose.material3.Text(
						text = "$label (${size.toInt()}dp)",
						fontSize = tokens.typography.caption1.fontSize.sp,
						color = parseColor(tokens.color.textMuted),
					)
				}
			}
		}

		Spacer(Modifier.height(tokens.spacing.xl.dp))

		// Min Touch Target
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
			modifier =
				Modifier
					.border(1.dp, parseColor(tokens.color.border), RoundedCornerShape(tokens.radius.md.dp))
					.padding(tokens.spacing.md.dp),
		) {
			Box(
				modifier =
					Modifier
						.size(tokens.sizing.minTouchTarget.dp)
						.border(2.dp, parseColor(tokens.color.danger), RoundedCornerShape(tokens.radius.sm.dp)),
				contentAlignment = Alignment.Center,
			) {
				androidx.compose.material3.Text(
					text = "${tokens.sizing.minTouchTarget.toInt()}dp",
					fontSize = tokens.typography.caption2.fontSize.sp,
					fontWeight = FontWeight.SemiBold,
					color = parseColor(tokens.color.danger),
				)
			}
			androidx.compose.material3.Text(
				text = "Min Touch Target (Apple HIG 44dp / Material 48dp)",
				fontSize = tokens.typography.footnote.fontSize.sp,
				color = parseColor(tokens.color.textSecondary),
			)
		}
	}
}

/* ─── Radius ─────────────────────────────────────────────── */

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RadiusShowcase(tokens: DesignTokens) {
	val entries =
		listOf(
			"xs" to tokens.radius.xs,
			"sm" to tokens.radius.sm,
			"md" to tokens.radius.md,
			"lg" to tokens.radius.lg,
			"xl" to tokens.radius.xl,
			"full" to tokens.radius.full,
		)
	ShowcaseSection("Radius", tokens) {
		FlowRow(
			horizontalArrangement = Arrangement.spacedBy(tokens.spacing.lg.dp),
			verticalArrangement = Arrangement.spacedBy(tokens.spacing.lg.dp),
		) {
			entries.forEach { (label, value) ->
				Column(horizontalAlignment = Alignment.CenterHorizontally) {
					Box(
						modifier =
							Modifier
								.size(56.dp)
								.clip(RoundedCornerShape(value.dp))
								.background(parseColor(tokens.color.primary)),
					)
					Spacer(Modifier.height(tokens.spacing.xs.dp))
					androidx.compose.material3.Text(
						text = "$label (${value.toInt()}dp)",
						fontSize = tokens.typography.caption1.fontSize.sp,
						color = parseColor(tokens.color.textMuted),
					)
				}
			}
		}
	}
}

/* ─── Motion ─────────────────────────────────────────────── */

@Composable
private fun MotionShowcase(tokens: DesignTokens) {
	var animate by remember { mutableStateOf(false) }
	val durations =
		listOf(
			"Instant" to tokens.motion.durationInstant,
			"Fast" to tokens.motion.durationFast,
			"Normal" to tokens.motion.durationNormal,
			"Slow" to tokens.motion.durationSlow,
			"Slower" to tokens.motion.durationSlower,
		)

	ShowcaseSection("Motion", tokens) {
		Button(
			text = if (animate) "Reset" else "Play Animation",
			variant = VisualVariant.Soft,
			onClick = { animate = !animate },
		)
		Spacer(Modifier.height(tokens.spacing.lg.dp))
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
			durations.forEach { (label, ms) ->
				val offsetX by animateDpAsState(
					targetValue = if (animate) 200.dp else 0.dp,
					animationSpec = tween(durationMillis = ms),
				)
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
				) {
					androidx.compose.material3.Text(
						text = "$label (${ms}ms)",
						fontSize = tokens.typography.caption1.fontSize.sp,
						color = parseColor(tokens.color.textMuted),
						modifier = Modifier.width(100.dp),
						textAlign = TextAlign.End,
					)
					Box(
						modifier =
							Modifier
								.offset(x = offsetX)
								.size(40.dp)
								.clip(RoundedCornerShape(tokens.radius.md.dp))
								.background(parseColor(tokens.color.primary)),
					)
				}
			}
		}
	}
}

/* ─── Breakpoints ────────────────────────────────────────── */

@Composable
private fun BreakpointsShowcase(tokens: DesignTokens) {
	val entries =
		listOf(
			"compact" to tokens.breakpoints.compact,
			"medium" to tokens.breakpoints.medium,
			"expanded" to tokens.breakpoints.expanded,
			"large" to tokens.breakpoints.large,
			"extraLarge" to tokens.breakpoints.extraLarge,
		)
	ShowcaseSection("Breakpoints (Material Design 3)", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.sm.dp)) {
			entries.forEach { (label, value) ->
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
				) {
					androidx.compose.material3.Text(
						text = label,
						fontSize = tokens.typography.caption1.fontSize.sp,
						color = parseColor(tokens.color.textMuted),
						modifier = Modifier.width(100.dp),
						textAlign = TextAlign.End,
					)
					androidx.compose.material3.Text(
						text = "${value.toInt()}dp",
						fontSize = tokens.typography.body.fontSize.sp,
						fontWeight = FontWeight.SemiBold,
						color = parseColor(tokens.color.textPrimary),
					)
				}
			}
		}
	}
}

/* ─── Size options for showcases ─────────────────────────── */

private val SIZE_OPTIONS = ComponentSize.entries.map { it.name to it.name.uppercase() }

private fun sizeFromId(id: String): ComponentSize =
	ComponentSize.entries.first { it.name == id }

private fun buttonSizeFromComponentSize(cs: ComponentSize): ButtonSize =
	ButtonSize.entries.first { it.name == cs.name }

/* ─── Button variants (merged with icons) ────────────────── */

private val ICON_POSITION_OPTIONS = listOf(
	"None" to "None",
	"Start" to "Start",
	"End" to "End",
	"Top" to "Top",
	"Bottom" to "Bottom",
	"StartEnd" to "Start+End",
)

private fun iconPositionFromId(id: String): IconPosition = when (id) {
	"Start" -> IconPosition.Start
	"End" -> IconPosition.End
	"Top" -> IconPosition.Top
	"Bottom" -> IconPosition.Bottom
	else -> IconPosition.None
}

@Composable
private fun ButtonShowcase(tokens: DesignTokens) {
	var selectedSizeId by remember { mutableStateOf(ComponentSize.Md.name) }
	var selectedPositionId by remember { mutableStateOf("None") }
	val selectedSize = buttonSizeFromComponentSize(sizeFromId(selectedSizeId))
	val selectedPosition = iconPositionFromId(selectedPositionId)
	val isStartEnd = selectedPositionId == "StartEnd"
	val hasIcons = selectedPositionId != "None"

	ShowcaseSection("Button", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xl.dp)) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
			) {
				androidx.compose.material3.Text(
					text = "Icon position:",
					fontSize = tokens.typography.caption1.fontSize.sp,
					color = parseColor(tokens.color.textMuted),
				)
				SegmentedControl(
					options = ICON_POSITION_OPTIONS,
					selectedId = selectedPositionId,
					onSelectionChange = { selectedPositionId = it },
				)
			}
			SegmentedControl(
				options = SIZE_OPTIONS,
				selectedId = selectedSizeId,
				onSelectionChange = { selectedSizeId = it },
			)

			VisualVariant.entries.forEach { variant ->
				Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
					androidx.compose.material3.Text(
						text = variant.name,
						fontSize = tokens.typography.headline.fontSize.sp,
						fontWeight = FontWeight.SemiBold,
						color = parseColor(tokens.color.textPrimary),
					)
					ColorIntent.entries.forEach { intent ->
						Row(
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.spacedBy(tokens.spacing.sm.dp),
						) {
							androidx.compose.material3.Text(
								text = "${intent.name}:",
								fontSize = tokens.typography.caption1.fontSize.sp,
								color = parseColor(tokens.color.textMuted),
								modifier = Modifier.width(60.dp),
							)
							Row(
								horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
								modifier = Modifier.horizontalScroll(rememberScrollState()),
							) {
								if (hasIcons) {
									if (isStartEnd) {
										Button(
											text = "Start+End",
											variant = variant,
											intent = intent,
											size = selectedSize,
											iconPosition = IconPosition.Start,
											iconStart = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) },
											iconEnd = { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null) },
										)
									} else {
										Button(
											text = "Кнопка",
											variant = variant,
											intent = intent,
											size = selectedSize,
											iconPosition = selectedPosition,
											iconStart = if (selectedPosition != IconPosition.End) ({ Icon(Icons.Filled.Search, contentDescription = null) }) else null,
											iconEnd = if (selectedPosition == IconPosition.End) ({ Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) }) else null,
										)
									}
									Button(
										text = "Disabled",
										variant = variant,
										intent = intent,
										size = selectedSize,
										iconPosition = if (isStartEnd) IconPosition.Start else selectedPosition,
										iconStart = { Icon(Icons.Filled.Star, contentDescription = null) },
										disabled = true,
									)
									Button(
										text = "Loading",
										variant = variant,
										intent = intent,
										size = selectedSize,
										iconPosition = selectedPosition,
										iconStart = { Icon(Icons.Filled.Check, contentDescription = null) },
										loading = true,
									)
								} else {
									Button(
										text = "${variant.name} ${selectedSize.name}",
										variant = variant,
										intent = intent,
										size = selectedSize,
									)
									Button(text = "Disabled", variant = variant, intent = intent, size = selectedSize, disabled = true)
									Button(text = "Loading", variant = variant, intent = intent, size = selectedSize, loading = true)
								}
							}
						}
					}
				}
			}
		}
	}
}

/* ─── Icon Button ────────────────────────────────────────── */

private val ICON_BUTTON_SAMPLES: List<@Composable () -> Unit> = listOf(
	{ Icon(Icons.Filled.Search, contentDescription = null) },
	{ Icon(Icons.Filled.Add, contentDescription = null) },
	{ Icon(Icons.Filled.Star, contentDescription = null) },
	{ Icon(Icons.Filled.Settings, contentDescription = null) },
	{ Icon(Icons.Filled.Close, contentDescription = null) },
	{ Icon(Icons.Filled.Check, contentDescription = null) },
)

@Composable
private fun IconButtonShowcase(tokens: DesignTokens) {
	var selectedSizeId by remember { mutableStateOf(ComponentSize.Md.name) }
	val selectedSize = sizeFromId(selectedSizeId)

	ShowcaseSection("Icon Button", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xl.dp)) {
			SegmentedControl(
				options = SIZE_OPTIONS,
				selectedId = selectedSizeId,
				onSelectionChange = { selectedSizeId = it },
			)

			VisualVariant.entries.forEach { variant ->
				Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
					androidx.compose.material3.Text(
						text = variant.name,
						fontSize = tokens.typography.headline.fontSize.sp,
						fontWeight = FontWeight.SemiBold,
						color = parseColor(tokens.color.textPrimary),
					)
					ColorIntent.entries.forEach { intent ->
						Row(
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.spacedBy(tokens.spacing.sm.dp),
						) {
							androidx.compose.material3.Text(
								text = "${intent.name}:",
								fontSize = tokens.typography.caption1.fontSize.sp,
								color = parseColor(tokens.color.textMuted),
								modifier = Modifier.width(60.dp),
							)
							Row(
								horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
								modifier = Modifier.horizontalScroll(rememberScrollState()),
							) {
								ICON_BUTTON_SAMPLES.forEach { iconSlot ->
									IconButton(
										icon = iconSlot,
										variant = variant,
										intent = intent,
										size = selectedSize,
									)
								}
								IconButton(
									icon = { Icon(Icons.Filled.Star, contentDescription = null) },
									variant = variant,
									intent = intent,
									size = selectedSize,
									disabled = true,
								)
								IconButton(
									icon = { Icon(Icons.Filled.Star, contentDescription = null) },
									variant = variant,
									intent = intent,
									size = selectedSize,
									loading = true,
								)
							}
						}
					}
				}
			}
		}
	}
}

/* ─── Surface variants ───────────────────────────────────── */

@Composable
private fun SurfaceShowcase(tokens: DesignTokens) {
	var surfaceMode by remember { mutableStateOf("default") }

	ShowcaseSection("Surface — Variant × Level", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.xl.dp)) {
			SegmentedControl(
				options = listOf(
					"default" to "Default",
					"hoverable" to "Hoverable",
					"clickable" to "Clickable",
				),
				selectedId = surfaceMode,
				onSelectionChange = { surfaceMode = it },
			)

			VisualVariant.entries.filter { it != VisualVariant.Ghost }.forEach { variant ->
				Column {
					androidx.compose.material3.Text(
						text = variant.name,
						fontSize = tokens.typography.headline.fontSize.sp,
						fontWeight = FontWeight.SemiBold,
						color = parseColor(tokens.color.textPrimary),
					)
					Spacer(Modifier.height(tokens.spacing.sm.dp))
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
									androidx.compose.material3.Text(
										text = level.name,
										fontSize = tokens.typography.caption1.fontSize.sp,
										fontWeight = FontWeight.SemiBold,
										color = parseColor(tokens.color.textPrimary),
									)
									Spacer(Modifier.height(tokens.spacing.xs.dp))
									androidx.compose.material3.Text(
										text = variant.name,
										fontSize = tokens.typography.caption2.fontSize.sp,
										color = parseColor(tokens.color.textMuted),
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

/* ─── Text variants ──────────────────────────────────────── */

@Composable
private fun TextShowcase(tokens: DesignTokens) {
	ShowcaseSection("Text Variants", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
			TextBlock(text = "Heading 1 — Заголовок", variant = TextBlockVariant.H1)
			TextBlock(text = "Heading 2 — Подзаголовок", variant = TextBlockVariant.H2)
			TextBlock(text = "Heading 3 — Секция", variant = TextBlockVariant.H3)
			TextBlock(
				text = "Body — Основной текст для чтения. The quick brown fox jumps over the lazy dog. مرحبا بالعالم. 你好世界",
				variant = TextBlockVariant.Body,
			)
			TextBlock(text = "Caption — Подпись к элементам", variant = TextBlockVariant.Caption)
		}

	}
}

/* ─── Segmented Control ──────────────────────────────────── */

@Composable
private fun SegmentedControlShowcase(tokens: DesignTokens) {
	var selected by remember { mutableStateOf("a") }
	var selectedSizeId by remember { mutableStateOf(ComponentSize.Sm.name) }
	val selectedSize = sizeFromId(selectedSizeId)

	ShowcaseSection("Segmented Control", tokens) {
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.lg.dp)) {
			// Size switcher
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
			) {
				androidx.compose.material3.Text(
					text = "Size:",
					fontSize = tokens.typography.caption1.fontSize.sp,
					color = parseColor(tokens.color.textMuted),
				)
				SegmentedControl(
					options = SIZE_OPTIONS,
					selectedId = selectedSizeId,
					onSelectionChange = { selectedSizeId = it },
				)
			}

			Column {
				androidx.compose.material3.Text(
					text = "3 options",
					fontSize = tokens.typography.footnote.fontSize.sp,
					color = parseColor(tokens.color.textMuted),
				)
				Spacer(Modifier.height(tokens.spacing.xs.dp))
				SegmentedControl(
					options = listOf("a" to "First", "b" to "Second", "c" to "Third"),
					selectedId = selected,
					onSelectionChange = { selected = it },
					size = selectedSize,
				)
			}
			Column {
				androidx.compose.material3.Text(
					text = "2 options",
					fontSize = tokens.typography.footnote.fontSize.sp,
					color = parseColor(tokens.color.textMuted),
				)
				Spacer(Modifier.height(tokens.spacing.xs.dp))
				SegmentedControl(
					options = listOf("on" to "Вкл", "off" to "Выкл"),
					selectedId = "on",
					onSelectionChange = {},
					size = selectedSize,
				)
			}
		}
	}
}

/* ─── Height Alignment Check ─────────────────────────────── */

@Composable
private fun HeightAlignmentShowcase(tokens: DesignTokens) {
	var selectedSizeId by remember { mutableStateOf(ComponentSize.Md.name) }
	val selectedSize = sizeFromId(selectedSizeId)
	val btnSize = buttonSizeFromComponentSize(selectedSize)
	val railColor = parseColor(tokens.color.outlineVariant)

	ShowcaseSection("Height Alignment Check", tokens) {
		Column(
			verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
		) {
			// Size selector
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
			) {
				androidx.compose.material3.Text(
					text = "Size:",
					fontSize = tokens.typography.footnote.fontSize.sp,
					fontWeight = FontWeight.SemiBold,
					color = parseColor(tokens.color.textMuted),
				)
				SegmentedControl(
					options = SIZE_OPTIONS,
					selectedId = selectedSizeId,
					onSelectionChange = { selectedSizeId = it },
				)
			}

			// Rails container — two horizontal dashed lines with components between them
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.drawBehind {
						val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f))
						// Top rail
						drawLine(
							color = railColor,
							start = Offset(0f, 0f),
							end = Offset(size.width, 0f),
							strokeWidth = 1f,
							pathEffect = dashEffect,
						)
						// Bottom rail
						drawLine(
							color = railColor,
							start = Offset(0f, size.height),
							end = Offset(size.width, size.height),
							strokeWidth = 1f,
							pathEffect = dashEffect,
						)
					},
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
					modifier = Modifier.horizontalScroll(rememberScrollState()),
				) {
					Button(
						text = "Solid",
						variant = VisualVariant.Solid,
						intent = ColorIntent.Primary,
						size = btnSize,
					)
					Button(
						text = "Outline",
						variant = VisualVariant.Outline,
						intent = ColorIntent.Neutral,
						size = btnSize,
					)
					Button(
						text = "Ghost",
						variant = VisualVariant.Ghost,
						intent = ColorIntent.Primary,
						size = btnSize,
					)
					Button(
						text = "With Icon",
						variant = VisualVariant.Solid,
						intent = ColorIntent.Primary,
						size = btnSize,
						iconPosition = IconPosition.Start,
						iconStart = { Icon(Icons.Filled.Search, contentDescription = null) },
					)
					IconButton(
						icon = { Icon(Icons.Filled.Star, contentDescription = null) },
						variant = VisualVariant.Solid,
						intent = ColorIntent.Primary,
						size = selectedSize,
					)
					SegmentedControl(
						options = listOf("a" to "On", "b" to "Off"),
						selectedId = "a",
						onSelectionChange = {},
						size = selectedSize,
						modifier = Modifier.width(160.dp),
					)
					TextBlock(
						text = "Text label",
						variant = TextBlockVariant.Body,
					)
				}
			}
		}
	}
}
