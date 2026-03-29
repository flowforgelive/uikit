import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.uikit.compose.theme.UIKitTheme

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "UIKit Catalog",
    ) {
        UIKitTheme {
            CatalogApp()
        }
    }
}
