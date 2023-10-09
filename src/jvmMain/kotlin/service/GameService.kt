package service

import model.GameAction
import model.GameState
import model.Player
import model.chat.Message
import retrofit2.Response
import retrofit2.http.*

interface GameService {

    @GET("game/room/{id}")
    suspend fun findGameRoomById(@Path("id") id: String): Response<GameState>

    @GET("game/room/all")
    suspend fun findAll(): Response<List<GameState>>


    @POST("game/room/matchmaking")
    suspend fun matchmaking(@Body player: Player): Response<GameState>

    @POST("game/room/create")
    suspend fun createRoom(@Body player: Player): Response<GameState>

    @POST("game/room/join")
    suspend fun joinInRoom(
        @Query("gameId") gameId: String,
        @Body player: Player,
    ): Response<GameState>

    @POST("game/room/action")
    suspend fun addMove(
        @Query("gameId") gameId: String,
        @Body gameAction: GameAction,
    ): Response<Unit>


    @POST("game/room/resetMatch")
    suspend fun resetMatch(
        @Query("gameId") gameId: String,
        @Query("playerId") playerId: String,
    ): Response<Unit>

    @POST("game/room/close")
    suspend fun closeMatch(
        @Query("gameId") gameId: String,
        @Query("playerId") playerId: String,
    ): Response<Unit>

    @POST("game/room/exit")
    suspend fun exitMatch(
        @Query("gameId") gameId: String,
        @Query("playerId") playerId: String,
    ): Response<Unit>

    @POST("game/room/chat")
    suspend fun sendMessage(@Body message: Message): Response<Unit>

}