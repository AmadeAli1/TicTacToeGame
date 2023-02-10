package ui.viewModel

import repository.OfflineGame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import model.GameType
import model.state.OfflineGameState
import utils.ViewModel

class OfflineGameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<OfflineGameState>(OfflineGameState.None)
    val uiState = _uiState.asStateFlow()

    val gameState = OfflineGame()

    fun sendGameAction(
        action: Pair<String, GameType>,
    ) {
        viewModelScope.launch {
            val playerWin = gameState.add(
                row = action.first[0].digitToInt(),
                column = action.first[1].digitToInt(),
                onFailureAction = {
                    viewModelScope.launch {
                        _uiState.emit(OfflineGameState.Failure(it))
                    }
                }
            )
            if (playerWin) {
                _uiState.emit(OfflineGameState.ShowWinPopUp(player = gameState.wonGames.lastOrNull()?.player))
            }
        }
    }

    fun closeMatch() {
        viewModelScope.launch {
            _uiState.emit(OfflineGameState.CloseMatch)
        }
    }

    fun nextMatch() {
        viewModelScope.launch {
            gameState.resetGame()
            _uiState.emit(OfflineGameState.NextMath)
        }
    }

    fun openExitGameDialog() {
        viewModelScope.launch {
            _uiState.emit(OfflineGameState.Dialog)
        }
    }

    fun onCloseDialog() {
        viewModelScope.launch {
            _uiState.emit(OfflineGameState.None)
        }
    }


}