package validation


object Validation {

    fun isValidUsername(username: String): ValidationState {
        if (username.isBlank()) return ValidationState.Failure("Must not be blank")
        if (username.length < 3) return ValidationState.Failure("Require 3 or more characters")
        return ValidationState.Success
    }

    fun isValidRoom(room: String): ValidationState {
        if (room.isBlank()) return ValidationState.Failure("Must not be blank")
        if (room.length < 13) return ValidationState.Failure("Require 13 or more characters")
        return ValidationState.Success
    }
}