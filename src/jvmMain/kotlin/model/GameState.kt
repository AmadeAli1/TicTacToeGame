package model

import kotlinx.serialization.Serializable
import model.chat.Message

@Serializable
data class GameState(
    val gameId: String,
    val player1: Player,
    var player2: Player? = null,
    var roomState: RoomState,
    var matchState: MatchState? = null,
    val expiredTime: String? = null,
    val messages: List<Message> = emptyList(),
)
