package repository

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import model.GameAction
import model.GameState
import model.Player
import model.chat.Message
import retrofit2.create
import service.GameService
import utils.RetrofitInstance
import utils.Server
import utils.toFailureMessage

object GameRepository {
    private val gameService: GameService by lazy { RetrofitInstance.retrofit.create() }

    private val client: HttpClient by lazy {
        HttpClient(CIO) {
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
        }
    }

    private var session: WebSocketSession? = null

    private val _player = MutableStateFlow<Player?>(null)
    val player = _player.asStateFlow()

    fun getGameStateFlow(gameId: String): Flow<GameState> {
        return flow {
            session = client.webSocketSession {
                url("${Server.Localhost.websocket}/$gameId")
            }
            val gameState = session!!.incoming
                .consumeAsFlow()
                .onEach {
                    println(it)
                }
                .filterIsInstance<Frame.Text>()
                .mapNotNull {
                    val readText = it.readText()
                    println(readText)
                    Json.Default.decodeFromString<GameState>(readText)
                }
            emitAll(gameState)
        }.flowOn(Dispatchers.IO).catch {
            System.err.println(it)
        }
    }

    suspend fun sendAction(
        gameId: String,
        gameAction: GameAction,
        onFailure: (String) -> Unit,
    ) = withContext(Dispatchers.IO) {
        try {
            val response = gameService.addMove(gameId = gameId, gameAction = gameAction)
            if (response.isSuccessful) {
            } else {
                response.toFailureMessage().also(onFailure)
            }
        } catch (e: Exception) {
            e.message?.let {
                if (it.startsWith("failed to connect").or(it.contains("timeout"))) {
                    onFailure("Check your network connection")
                }
            }
            e.printStackTrace()
        }
    }

    suspend fun createGameRoom(
        username: String,
        onLoading: () -> Unit,
        onSuccess: (GameState) -> Unit,
        onFailure: (String) -> Unit,
    ) = withContext(Dispatchers.IO) {
        try {
            onLoading()
            val response = gameService.createRoom(Player(name = username))
            if (response.isSuccessful) {
                val gameState = response.body()!!
                _player.emit(gameState.player1)
                onSuccess(gameState)
            } else {
                response.toFailureMessage().also(onFailure)
            }
        } catch (e: Exception) {
            e.message?.let {
                if (it.startsWith("Failed to connect", ignoreCase = true)
                        .or(it.contains("Timeout", ignoreCase = true))
                ) {
                    onFailure("Check your network connection")
                }
            }
            e.printStackTrace()
        }
    }

    suspend fun matchMaking(
        onLoading: () -> Unit,
        onSuccess: (GameState) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        try {
            onLoading()
            val username = System.getProperties().getProperty("user.name") ?: "Unknown"
            val response = gameService.matchmaking(player = Player(name = username))
            if (response.isSuccessful) {
                val gameState = response.body()!!
                _player.emit(gameState.player2)
                onSuccess(gameState)
            } else {
                response.toFailureMessage().also(onFailure)
            }
        } catch (e: Exception) {
            e.message?.let {
                if (it.startsWith("failed to connect").or(it.contains("timeout"))) {
                    onFailure("Check your network connection")
                } else {
                    onFailure(it)
                }
            }
            e.printStackTrace()
        }
    }

    suspend fun joinInRoom(
        gameId: String,
        username: String,
        onLoading: () -> Unit,
        onSuccess: (GameState) -> Unit,
        onFailure: (String) -> Unit,
    ) = withContext(Dispatchers.IO) {
        try {
            onLoading()
            val response = gameService.joinInRoom(gameId = gameId, player = Player(name = username))
            if (response.isSuccessful) {
                val gameState = response.body()!!
                _player.emit(gameState.player2)
                onSuccess(gameState)
            } else {
                response.toFailureMessage().also(onFailure)
            }
        } catch (e: Exception) {
            e.message?.let {
                if (it.startsWith("failed to connect").or(it.contains("timeout"))) {
                    onFailure("Check your network connection")
                } else {
                    onFailure(it)
                }
            }
            e.printStackTrace()
        }
    }

    suspend fun closeMatch(
        gameId: String,
        playerId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) = withContext(Dispatchers.IO) {

        try {
            val response = gameService.closeMatch(gameId, playerId)
            if (response.isSuccessful) {
                onSuccess()
            } else {
                response.toFailureMessage().also(onFailure)
            }
        } catch (e: Exception) {
            e.message?.let {
                if (it.startsWith("failed to connect").or(it.contains("timeout"))) {
                    onFailure("Check your network connection")
                }
            }
            e.printStackTrace()
        }
    }

    suspend fun exitMatch(
        gameId: String,
        playerId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) = withContext(Dispatchers.IO) {

        try {
            val response = gameService.exitMatch(gameId, playerId)
            if (response.isSuccessful) {
                onSuccess()
            } else {
                response.toFailureMessage().also(onFailure)
            }
        } catch (e: Exception) {
            e.message?.let {
                if (it.startsWith("failed to connect").or(it.contains("timeout"))) {
                    onFailure("Check your network connection")
                }
            }
            e.printStackTrace()
        }
    }


    suspend fun resetMatch(
        gameId: String,
        playerId: String,
        onFailure: (String) -> Unit,
    ) = withContext(Dispatchers.IO) {

        try {
            val response = gameService.resetMatch(gameId, playerId)
            if (response.isSuccessful) {
            } else {
                response.toFailureMessage().also(onFailure)
            }
        } catch (e: Exception) {
            e.message?.let {
                if (it.startsWith("failed to connect").or(it.contains("timeout"))) {
                    onFailure("Check your network connection")
                }
            }
            e.printStackTrace()
        }
    }


    suspend fun sendMessage(roomId: String, message: String) {
        withContext(Dispatchers.IO) {
            gameService.sendMessage(
                message = Message(
                    from = player.value!!.id,
                    roomId = roomId,
                    message = message
                )
            )
        }
    }

    suspend fun closeSession() {
        session?.close()
    }


}