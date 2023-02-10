package ui.navigation

sealed interface Screen {
    object Splash : Screen
    object Home : Screen
    object Offline : Screen
    object CreateRoom : Screen
    object JoinRoom : Screen
    data class GameRoom(val id: String) : Screen
}
