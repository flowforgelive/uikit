import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() =
	application {
		Window(
			onCloseRequest = ::exitApplication,
			title = "UIKit Catalog",
		) {
			val interFontFamily = remember {
				FontFamily(
					Font(resource = "fonts/InterVariable.ttf", weight = FontWeight.Normal),
					Font(resource = "fonts/InterVariable.ttf", weight = FontWeight.Medium),
					Font(resource = "fonts/InterVariable.ttf", weight = FontWeight.SemiBold),
					Font(resource = "fonts/InterVariable.ttf", weight = FontWeight.Bold),
				)
			}
			CatalogApp(fontFamily = interFontFamily)
		}
	}
