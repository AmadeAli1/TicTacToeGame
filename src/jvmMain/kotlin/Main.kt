import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import ui.screen.EntryPoint
import ui.theme.GameTheme

/**
 * @author Amade ALi
 * @since 2023
 */

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = WindowState(placement = WindowPlacement.Maximized),
        title = "TicTacToe", icon = painterResource("icon.png")
    ) {
        GameTheme {
            Surface(color = MaterialTheme.colorScheme.background) {
                EntryPoint()
            }
        }
    }
}
