package model

import kotlinx.serialization.Serializable
import model.Player

@Serializable
data class MatchDetail(
    val player: Player,
    var winning: Int = 0,
    var loses: Int = 0,
    var draw: Int = 0,
)
