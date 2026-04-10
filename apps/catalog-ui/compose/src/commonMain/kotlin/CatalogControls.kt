import androidx.compose.runtime.Composable
import catalog.CatalogOptions
import com.uikit.compose.components.composites.segmentedcontrol.SegmentedControl
import com.uikit.foundation.ComponentSize
import com.uikit.foundation.LayoutDirection
import com.uikit.foundation.ThemeMode

enum class BackgroundMode { Dots, Image, Solid }

private val BG_OPTIONS = listOf(
	"dots" to "Точки",
	"image" to "Фото",
	"solid" to "Сплошной",
)

@Composable
internal fun ThemeSwitcherControl(
	currentMode: ThemeMode,
	onThemeChange: (String) -> Unit,
) {
	SegmentedControl(
		options = CatalogOptions.themeOptions.map { it.id to it.label },
		selectedId =
			when (currentMode) {
				ThemeMode.Dark -> "dark"
				ThemeMode.Light -> "light"
				ThemeMode.System -> "system"
			},
		onSelectionChange = onThemeChange,
	)
}

@Composable
internal fun DirSwitcherControl(
	currentDir: LayoutDirection,
	onDirChange: (LayoutDirection) -> Unit,
) {
	SegmentedControl(
		options = CatalogOptions.dirOptions.map { it.id to it.label },
		selectedId = if (currentDir == LayoutDirection.Ltr) "ltr" else "rtl",
		onSelectionChange = { id ->
			onDirChange(if (id == "rtl") LayoutDirection.Rtl else LayoutDirection.Ltr)
		},
	)
}

@Composable
internal fun BgSwitcherControl(
	currentBg: BackgroundMode,
	onBgChange: (BackgroundMode) -> Unit,
) {
	SegmentedControl(
		options = BG_OPTIONS,
		selectedId = when (currentBg) {
			BackgroundMode.Dots -> "dots"
			BackgroundMode.Image -> "image"
			BackgroundMode.Solid -> "solid"
		},
		onSelectionChange = { id ->
			onBgChange(
				when (id) {
					"image" -> BackgroundMode.Image
					"solid" -> BackgroundMode.Solid
					else -> BackgroundMode.Dots
				},
			)
		},
		size = ComponentSize.Sm,
	)
}
