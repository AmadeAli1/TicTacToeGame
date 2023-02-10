package ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import ui.navigation.MainNavigationController
import ui.navigation.Screen

@Composable
fun EntryPoint() {
    val controller = remember { MainNavigationController }
    val screenState by controller.screen.collectAsState()
    when (screenState) {
        is Screen.CreateRoom -> {
            CreateGameRoom()
        }

        is Screen.GameRoom -> {
            GameRoomScreen()
        }

        is Screen.Home -> {
            HomeScreen()
        }

        is Screen.Offline -> {
            OfflineGameScreen()
        }

        is Screen.JoinRoom -> {
            JoinRoomScreen()
        }

        is Screen.Splash -> {
            SplashScreen()
        }
    }
}