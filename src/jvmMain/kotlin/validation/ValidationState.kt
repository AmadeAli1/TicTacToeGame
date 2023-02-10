package validation

sealed class ValidationState(val message: String) {
    object Success : ValidationState(message = "Success")
    data class Failure(private val msg: String) : ValidationState(msg)
}
