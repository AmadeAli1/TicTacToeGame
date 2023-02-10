package model.state

sealed class UiState {
    object None : UiState()
    data class Failure(val message: String) : UiState()
}
