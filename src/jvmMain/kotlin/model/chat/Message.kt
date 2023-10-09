package model.chat

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Long = System.currentTimeMillis(),
    val to: String = "",
    val from: String = "",
    val roomId: String = "",
    val message: String = "",
)
