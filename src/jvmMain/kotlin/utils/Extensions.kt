package utils

import com.google.gson.Gson
import retrofit2.Response


inline fun <reified T> String.toObject(): T {
    return Gson().fromJson<T>(this, T::class.java)
}

fun <T> Response<T>.toFailureMessage(): String {
    return try {
        val json = this.errorBody()!!.string().toObject<ApiError>()
        json.message
    } catch (e: Exception) {
        "Internal Server Error"
    }
}

data class ApiError(
    val message: String,
)
