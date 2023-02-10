package model.state

sealed class SearchGameState {
    object None : SearchGameState()
    object Loading : SearchGameState()
    object Success : SearchGameState()
    data class Failure(val message: String) : SearchGameState()
}