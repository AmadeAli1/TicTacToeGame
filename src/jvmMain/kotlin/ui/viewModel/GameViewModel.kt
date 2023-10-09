package ui.viewModel

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import model.*
import model.state.UiState
import repository.GameRepository
import ui.navigation.MainNavigationController
import ui.navigation.Screen
import utils.ViewModel

class GameViewModel : ViewModel() {
    private val repository: GameRepository = GameRepository
    private val gameId = (MainNavigationController.screen.value as Screen.GameRoom).id
    val player = repository.player

    private val _roomState = MutableStateFlow(RoomState.Joining)
    val roomState = _roomState.asStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(15_000),
            initialValue = RoomState.Joining
        )

    private val _uiState = MutableStateFlow<UiState>(UiState.None)
    val uiState = _uiState.asStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(15_000),
            initialValue = UiState.None
        )

    val gameState = repository.getGameStateFlow(gameId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(30_000),
        initialValue = null
    ).onEach {
        if (it != null) {
            _roomState.emit(it.roomState)
            if (it.roomState == RoomState.ExpiredSeason) {
                repository.closeSession()
            }
        }
    }

    fun sendGameAction(
        matchState: MatchState,
        action: Pair<String, GameType>,
    ) {
        viewModelScope.launch {
            if (matchState.currentPlayer == player.value) {
                repository.sendAction(
                    gameId = gameId,
                    gameAction = GameAction(
                        row = action.first[0].digitToInt(),
                        column = action.first[1].digitToInt(),
                        playerId = player.value!!.id
                    ),
                    onFailure = {
                        viewModelScope.launch {
                            _uiState.emit(UiState.Failure(it))
                            delay(3000)
                            _uiState.emit(UiState.None)
                        }
                    }
                )
            }
        }
    }

    fun closeMatch() {
        viewModelScope.launch {
            repository.closeMatch(
                gameId = gameId,
                playerId = player.value!!.id,
                onSuccess = {
                    viewModelScope.launch {
                        _roomState.emit(RoomState.Closed)
                        repository.closeSession()
                    }
                },
                onFailure = {
                    viewModelScope.launch {
                        _uiState.emit(UiState.Failure(it))
                    }
                }
            )
        }
    }

    fun exitGame() {
        viewModelScope.launch {
            repository.exitMatch(
                gameId = gameId,
                playerId = player.value!!.id,
                onSuccess = {
                    viewModelScope.launch {
                        _roomState.emit(RoomState.Playing)
                        repository.closeSession()
                        println("exitGame: ${RoomState.Playing}")
                    }
                },
                onFailure = {
                    viewModelScope.launch {
                        _uiState.emit(UiState.Failure(it))
                    }
                }
            )
        }
    }

    fun resetMatch() {
        viewModelScope.launch {
            repository.resetMatch(
                gameId = gameId,
                playerId = player.value!!.id,
                onFailure = {
                    viewModelScope.launch {
                        _uiState.emit(UiState.Failure(it))
                    }
                }
            )
        }
    }

    fun getWinner(matchState: MatchState): Player {
        return matchState.wonGames.last().player
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            repository.sendMessage(gameId,message)
        }
    }

}