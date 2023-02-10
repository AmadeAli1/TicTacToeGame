package model

import kotlinx.serialization.Serializable
import model.GameType
import model.MatchDetail
import model.Player

@Serializable
data class MatchState(
    val round: Int,
    val currentPlayer: Player,
    val player1Details: MatchDetail,
    val player2Details: MatchDetail,
    val tableState: MutableMap<String, GameType>,
    val wonGames: List<MatchDetail>,
)
