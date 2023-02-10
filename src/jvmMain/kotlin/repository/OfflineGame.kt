package repository

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import model.GameType
import model.MatchDetail
import model.Player

data class OfflineGame(
    private var currentPlayer: Player = Player("one", name = "player one"),
    val wonGames: MutableList<MatchDetail> = mutableListOf(),
    val moves: Int = 0,
) {
    private var size = 0
    private val table = Array(3) { arrayOfNulls<GameType>(3) }

    //tableState is a var
    val tableState: SnapshotStateMap<String, GameType>

    private val _player1Details =
        mutableStateOf(MatchDetail(player = Player("one", "Player one")))

    private val _player2Details =
        mutableStateOf(MatchDetail(player = Player("two", "Player two", GameType.O)))

    val player1Details: State<MatchDetail> = _player1Details
    val player2Details: State<MatchDetail> = _player2Details


    val round = mutableStateOf(1)

    private fun startGame() = mutableStateMapOf<String, GameType>().apply {
        ticTacInitialize()
    }

    private fun startTable() {
        for (i in 0..2) {
            for (j in 0..2) {
                table[i][j] = GameType.Empty
            }
        }
    }

    init {
        tableState = startGame()
        startTable()
    }

    fun add(
        row: Int,
        column: Int,
        onFailureAction: (String) -> Unit,
    ): Boolean {
        if (table[row][column] == GameType.O || table[row][column] == GameType.X) {
            onFailureAction("Tente em outro local")
            return false
        }
        println("Current player: [${currentPlayer.gameType}]")

        table[row][column] = currentPlayer.gameType
        val block: (MutableMap<String, GameType>) -> Unit = {
            it["${row}${column}"] = currentPlayer.gameType
        }
        //tableState  =
        tableState.apply(block)
        val verify = verify(currentPlayer.gameType, row, column)

        if (verify) {
            if (currentPlayer == player1Details.value.player) {
                _player1Details.value = _player1Details.value.copy(
                    winning = player1Details.value.winning + 1
                )
                //player1Details.winning = player1Details.winning + 1

                _player2Details.value = _player2Details.value.copy(
                    loses = player2Details.value.loses + 1
                )
                //player2Details.loses = player2Details.loses + 1
                wonGames.add(player1Details.value)
            } else {
                _player2Details.value = _player2Details.value.copy(
                    winning = player2Details.value.winning + 1
                )
                //player1Details.winning = player1Details.winning + 1

                _player1Details.value = _player1Details.value.copy(
                    loses = player1Details.value.loses + 1
                )
                //player2Details.loses = player2Details.loses + 1
                wonGames.add(_player2Details.value)
                //player2Details.winning = player2Details.winning + 1
                //player1Details.loses = player1Details.loses + 1
                //wonGames.add(player2Details)
            }
            currentPlayer = if (currentPlayer.id == _player1Details.value.player.id) {
                _player2Details.value.player
            } else {
                player1Details.value.player
            }
            return true
        }

        currentPlayer = if (currentPlayer.id == _player1Details.value.player.id) {
            _player2Details.value.player
        } else {
            player1Details.value.player
        }

        size++
        if (isFull()) {
            println("Nao existe um vencedor")
            _player1Details.value = _player1Details.value.copy(
                draw = player1Details.value.draw + 1
            )
            _player2Details.value = _player2Details.value.copy(
                draw = player2Details.value.draw + 1
            )
            //player1Details.value = player1Details.draw + 1
            //player2Details.draw = player2Details.draw + 1
            resetGame()
        }
        return false
    }

    fun resetGame() {
        tableState.apply {
            clear()
            ticTacInitialize()
        }
        size = 0
        round.value = round.value + 1
        startTable()
    }

    private fun SnapshotStateMap<String, GameType>.ticTacInitialize() {
        put("00", GameType.Empty)
        put("01", GameType.Empty)
        put("02", GameType.Empty)
        put("10", GameType.Empty)
        put("11", GameType.Empty)
        put("12", GameType.Empty)
        put("20", GameType.Empty)
        put("21", GameType.Empty)
        put("22", GameType.Empty)
    }

    private fun isFull(): Boolean {
        return size == 9
    }

    private fun verify(input: GameType, row: Int, column: Int): Boolean {
        var state = verifyRow(input, row)
        if (!state) {
            state = verifyColumn(input, column)
        }
        if (!state) {
            state = verifyDiagonalPrincipal(input)
        }
        if (!state) {
            state = verifyDiagonalSecundaria(input)
        }
        return state
    }

    private fun verifyDiagonalSecundaria(input: GameType): Boolean {
        var state = true
        for (i in 0..2) {
            for (j in 0..2) {
                if (i + j == 3 - 1) {
                    if (table[i][j] != input) {
                        state = false
                        break
                    }
                }
            }
        }
        return state
    }

    private fun verifyDiagonalPrincipal(input: GameType): Boolean {
        var state = true
        for (i in 0..2) {
            for (j in 0..2) {
                if (i == j) {
                    if (table[i][j] != input) {
                        state = false
                        break
                    }
                }
            }
        }
        return state
    }

    private fun verifyRow(input: GameType, row: Int): Boolean {
        var state = true
        for (j in 0..2) {
            val data = table[row][j]
            if (data != input) {
                state = false
                break
            }
        }
        return state
    }

    private fun verifyColumn(input: GameType, column: Int): Boolean {
        var state = true
        for (i in 0..2) {
            val data = table[i][column]
            if (data != input) {
                state = false
                break
            }
        }
        return state
    }
}