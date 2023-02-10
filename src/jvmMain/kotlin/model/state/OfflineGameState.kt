package model.state

import model.Player

sealed class OfflineGameState {
    object None : OfflineGameState()
    data class Failure(val message: String) : OfflineGameState()
    data class ShowWinPopUp(val player: Player? = null) : OfflineGameState()
    object CloseMatch : OfflineGameState()
    object NextMath : OfflineGameState()
    object Dialog : OfflineGameState()
}