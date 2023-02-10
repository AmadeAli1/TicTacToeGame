package model

import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val gameId: String,
    val player1: Player,
    var player2: Player? = null,
    var roomState: RoomState,
    var matchState: MatchState? = null,
    val expiredTime: String? = null,
)
