package utils

sealed class Server(val url: String, val websocket: String) {
    object Localhost : Server("http://localhost:8080/api/", "ws://localhost:8080/api/ws/game")
    object Deploy : Server(
        "https://gametictactoe-production.up.railway.app/api/",
        "wss://gametictactoe-production.up.railway.app/api/ws/game"
    )
}
