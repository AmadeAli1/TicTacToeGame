package model.state

sealed class GameRoomState {
    object None : GameRoomState()
    object Loading : GameRoomState()
    data class Success(val gameId: String) : GameRoomState()
    data class Failure(val message: String) : GameRoomState()
}