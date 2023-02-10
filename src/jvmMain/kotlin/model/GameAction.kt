package model

@kotlinx.serialization.Serializable
data class GameAction(
    val row: Int,
    val column: Int,
    val playerId: String,
)
