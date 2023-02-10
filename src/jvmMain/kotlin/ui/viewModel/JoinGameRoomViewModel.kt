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
import java.util.function.Predicate

class JoinGameRoomViewModel(
    private val validation: Validation = Validation,
    private val repository: GameRepository = GameRepository,
) : ViewModel() {

    private val _username = mutableStateOf("")
    val username: State<String> = _username

    private val _room = mutableStateOf("")
    val room: State<String> = _room

    private val _usernameValidation = MutableStateFlow<ValidationState>(ValidationState.Success)
    val usernameValidation = _usernameValidation.asStateFlow()

    private val _roomValidation = MutableStateFlow<ValidationState>(ValidationState.Success)
    val roomValidation = _roomValidation.asStateFlow()

    private val _uiState = MutableStateFlow<GameRoomState>(GameRoomState.None)
    val uiState = _uiState.asStateFlow()


    fun onUsernameChange(username: String) {
        _username.value = username
        viewModelScope.launch {
            _usernameValidation.emit(validation.isValidUsername(username))
        }
    }

    fun onRoomChange(room: String) {
        _room.value = room.uppercase()
        viewModelScope.launch {
            _roomValidation.emit(validation.isValidRoom(room))
        }
    }

    private fun isValid(): Boolean {
        _usernameValidation.value = validation.isValidUsername(username.value)
        _roomValidation.value = validation.isValidRoom(room.value)
        val predicate = Predicate<ValidationState> {
            _usernameValidation.value == it
        }.and {
            _roomValidation.value == it
        }
        return predicate.test(ValidationState.Success)
    }

    fun join() {
        viewModelScope.launch {
            if (isValid()) {
                repository.joinInRoom(
                    gameId = room.value,
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
                            delay(5000)
                            _uiState.emit(GameRoomState.None)
                        }
                    }
                )


            }

        }
    }
}