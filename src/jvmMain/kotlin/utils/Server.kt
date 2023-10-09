package utils

sealed class Server(val url: String, val websocket: String) {
    object Localhost : Server("http://localhost:8099/api/", "ws://localhost:8099/api/ws/game")
    object Deploy : Server(
        " https://81e5-41-220-201-147.in.ngrok.io/api/",
        "wss://81e5-41-220-201-147.in.ngrok.io/api/ws/game"
    )
}
