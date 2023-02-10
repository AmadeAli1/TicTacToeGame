package ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import model.state.GameRoomState
import repository.GameRepository
import utils.ViewModel
import validation.Validation
import validation.ValidationState

class CreateRoomViewModel constructor(
    private val validation: Validation = Validation,
    private val repository: GameRepository = GameRepository,
) : ViewModel() {

    private val _username = mutableStateOf("")
    val username: State<String> = _username

    private val _usernameValidation = MutableStateFlow<ValidationState>(ValidationState.Success)
    val usernameValidation = _usernameValidation.asStateFlow()

    private val _uiState = MutableStateFlow<GameRoomState>(GameRoomState.None)
    val uiState = _uiState.asStateFlow()

    fun onUsernameChange(username: String) {
        _username.value = username
        viewModelScope.launch {
            _usernameValidation.emit(validation.isValidUsername(username))
        }
    }

    private fun isValid(): Boolean {
        _usernameValidation.value = validation.isValidUsername(username.value)
        return _usernameValidation.value == ValidationState.Success
    }

    fun createGame() {
        viewModelScope.launch {
            if (isValid()) {
                repository.createGameRoom(
                    username = username.value,
                    onLoading = {
                        viewModelScope.launch {
                            _uiState.emit(GameRoomState.Loading)
                        }
                    },
                    onSuccess = {
                        viewModelScope.launch {
                            _uiState.emit(
                                value = GameRoomState.Success(it.gameId)
                            )
                        }
                    },
                    onFailure = {
                        viewModelScope.launch {
                            _uiState.emit(GameRoomState.Failure(it))
                            delay(3000)
                            _uiState.emit(GameRoomState.None)
                        }
                    }
                )
            }
        }
    }
}