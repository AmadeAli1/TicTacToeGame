package model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: String = "",
    val name: String,
    val gameType: GameType = GameType.X,
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Player

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
