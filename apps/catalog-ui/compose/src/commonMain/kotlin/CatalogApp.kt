import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uikit.components.atoms.button.ButtonVariant
import com.uikit.components.atoms.text.TextBlockVariant
import com.uikit.compose.components.atoms.button.ButtonView
import com.uikit.compose.components.atoms.text.TextBlockView

@Composable
fun CatalogApp() {
    var currentScreen by remember { mutableStateOf("first") }

    val onAction: (String) -> Unit = { route ->
        currentScreen = route.removePrefix("/")
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (currentScreen) {
            "first" -> FirstScreen(onAction)
            "second" -> SecondScreen(onAction)
        }
    }
}

@Composable
private fun FirstScreen(onAction: (String) -> Unit) {
    TextBlockView(
        text = "Первая страница",
        variant = TextBlockVariant.H1,
    )

    Spacer(Modifier.height(24.dp))

    ButtonView(
        text = "Перейти на вторую страницу",
        onClick = { onAction("/second") },
    )
}

@Composable
private fun SecondScreen(onAction: (String) -> Unit) {
    TextBlockView(
        text = "Вторая страница",
        variant = TextBlockVariant.H1,
    )

    Spacer(Modifier.height(24.dp))

    ButtonView(
        text = "Вернуться назад",
        variant = ButtonVariant.Secondary,
        onClick = { onAction("/first") },
    )
}
