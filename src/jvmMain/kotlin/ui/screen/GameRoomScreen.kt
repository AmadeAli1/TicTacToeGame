package ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import model.*
import model.state.UiState
import org.jetbrains.skiko.Cursor
import ui.navigation.MainNavigationController
import ui.navigation.Screen
import ui.theme.blueColor
import ui.theme.buttonColor1
import ui.theme.color200
import ui.theme.firstCardColor
import ui.viewModel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameRoomScreen() {
    val controller = remember { MainNavigationController }
    val gameViewModel = remember { GameViewModel() }
    val roomState by gameViewModel.roomState.collectAsState()
    val gameState by gameViewModel.gameState.collectAsState(initial = null)
    val uiState by gameViewModel.uiState.collectAsState()
    val currentPlayer by gameViewModel.player.collectAsState()
    LaunchedEffect(key1 = roomState) {
        if (roomState == RoomState.Closed || roomState == RoomState.ExpiredSeason) {
            controller.navigate(route = Screen.Home)
        }
    }
    Scaffold(
        topBar = {
            gameState?.let {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Surface(
                        onClick = {
                            controller.navigate(route = Screen.Home)
                            gameViewModel.closeMatch()
                        },
                        shape = MaterialTheme.shapes.small,
                        shadowElevation = 4.dp,
                        modifier = Modifier.size(70.dp)
                            .padding(16.dp)
                            .align(Alignment.TopStart), color = firstCardColor
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu, contentDescription = null,
                            tint = color200
                        )
                    }

                    if (it.player2 != null) {
                        TopBar(
                            player1 = it.matchState!!.player1Details,
                            player2 = it.matchState!!.player2Details
                        )
                    }

                }
            }
        },
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            if (uiState is UiState.Failure) {
                Popup(alignment = Alignment.TopCenter, onDismissRequest = {}) {
                    (uiState as? UiState.Failure)?.message?.let {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = firstCardColor),
                            shape = MaterialTheme.shapes.small, modifier = Modifier.padding(16.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(0.35f).padding(16.dp)) {
                                Text(it)
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.34f)
            ) {
                gameState?.matchState?.tableState?.let { table ->
                    GameTable(tableState = table,
                        onGameAction = { action ->
                            gameViewModel.sendGameAction(
                                matchState = gameState!!.matchState!!,
                                action = action
                            )
                        }
                    )
                }

            }

            gameState?.let { game ->
                if (game.player2 != null) {
                    MessageContainer(
                        gameState = game,
                        currentPlayer = currentPlayer!!,
                        sendMessage = { message ->
                            gameViewModel.sendMessage(message)
                        }, modifier = Modifier
                            .align(Alignment.TopEnd)
                            .width(380.dp)
                            .fillMaxHeight()
                            .padding(16.dp)
                    )
                }
            }
            if (roomState == RoomState.Joining) {
                DialogWaitAnotherPlayer(gameState?.gameId)
            }
            if (roomState == RoomState.WaitForNextMatch) {
                GameWinDialog(
                    player = gameViewModel.getWinner(gameState!!.matchState!!),
                    onClose = gameViewModel::closeMatch,
                    onContinue = gameViewModel::resetMatch
                )
            }
        }

    }


}

@Composable
fun DialogWaitAnotherPlayer(gameId: String?) {
    Popup(onDismissRequest = { /*TODO*/ }, alignment = Alignment.Center) {
        Card(
            modifier = Modifier
                .width(280.dp)
                .height(240.dp)
                .animateContentSize(),
            colors = CardDefaults.cardColors(containerColor = firstCardColor),
            shape = MaterialTheme.shapes.small
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Gamepad,
                    contentDescription = null,
                    tint = buttonColor1, modifier = Modifier.size(42.dp)
                )
                Spacer(modifier = Modifier.padding(8.dp))

                Text(
                    text = "Esperando outro jogador",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                gameId?.let {
                    Spacer(modifier = Modifier.padding(3.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Room:",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                        )
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(8.dp))
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.padding(top = 16.dp))

            }
        }
    }
}

@Composable
fun TopBar(
    player1: MatchDetail, player2: MatchDetail,
    modifier: Modifier = Modifier.fillMaxWidth(0.34f),
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .then(modifier)
                .align(Alignment.TopCenter)
                .padding(top = 22.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                shape = MaterialTheme.shapes.small,
                colors = CardDefaults.cardColors(containerColor = firstCardColor),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = blueColor
                    )

                    Text(
                        text = player1.player.name,
                        style = MaterialTheme.typography.titleMedium.copy(color = blueColor)
                    )
                }
            }

            Text(
                text = "${player1.winning} - ${player2.winning}", fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )


            Card(
                shape = MaterialTheme.shapes.small,
                colors = CardDefaults.cardColors(containerColor = firstCardColor),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = buttonColor1
                    )

                    Text(
                        text = player2.player.name,
                        style = MaterialTheme.typography.titleMedium.copy(color = buttonColor1),
                    )
                }
            }
        }
    }
}

@Composable
fun BottomBar(round: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Card(
            shape = MaterialTheme.shapes.small,
            colors = CardDefaults.cardColors(containerColor = firstCardColor.copy(alpha = 0.4f)),
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Round  $round",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                )
            }
        }
    }

}

@Composable
fun BoxScope.GameTable(
    tableState: Map<String, GameType>,
    onGameAction: (Pair<String, GameType>) -> Unit,
) {
    val lazyGridState = rememberLazyGridState()
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .align(Alignment.Center),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        state = lazyGridState,
        contentPadding = PaddingValues(10.dp),
        content = {
            items(items = tableState.toSortedMap().toList()) { data ->
                GameInput(onClick = { onGameAction(data) }, state = data)
            }
        }
    )
}

@Composable
fun BoxScope.GameTable(
    tableState: SnapshotStateMap<String, GameType>,
    onGameAction: (Pair<String, GameType>) -> Unit,
) {
    val lazyGridState = rememberLazyGridState()
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .align(Alignment.Center),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        state = lazyGridState,
        contentPadding = PaddingValues(10.dp),
        content = {
            items(items = tableState.toSortedMap().toList()) { data ->
                GameInput(onClick = { onGameAction(data) }, state = data)
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageContainer(
    modifier: Modifier = Modifier,
    gameState: GameState,
    currentPlayer: Player,
    sendMessage: (String) -> Unit,
) {
    Scaffold(
        bottomBar = {
            MessageInput(
                onSendClick = sendMessage
            )
        },
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(items = gameState.messages) { message ->
                MessageBubble(
                    isMyMessage = message.from == currentPlayer.id,
                    message = message.message
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInput(onSendClick: (String) -> Unit) {
    var text by remember {
        mutableStateOf("")
    }
    TextField(
        value = text,
        onValueChange = {
            text = it
        }, placeholder = {
            Text(text = "Your message here")
        },
        trailingIcon = {
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onSendClick(text)
                        text = ""
                    }
                },
                modifier = Modifier.padding(end = 5.dp)
                    .pointerHoverIcon(icon = PointerIcon(Cursor(Cursor.HAND_CURSOR))),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor1,
                    contentColor = firstCardColor
                ), shape = MaterialTheme.shapes.extraLarge
            ) {
                Text(text = "Send")
            }
        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = firstCardColor,
            unfocusedIndicatorColor = Color.Unspecified,
            focusedIndicatorColor = Color.Unspecified,
            cursorColor = buttonColor1,
            textColor = buttonColor1,
        ),
        textStyle = MaterialTheme.typography.titleMedium,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun MessageBubble(isMyMessage: Boolean, message: String) {
    val config = if (!isMyMessage) {
        Pair(
            RoundedCornerShape(
                topStart = 30.dp,
                bottomStart = 0.dp,
                topEnd = 30.dp,
                bottomEnd = 30.dp,
            ),
            Alignment.Start
        )
    } else {
        Pair(
            RoundedCornerShape(
                topStart = 30.dp,
                bottomStart = 30.dp,
                topEnd = 30.dp,
                bottomEnd = 0.dp,
            ),

            Alignment.End
        )
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            tonalElevation = if (isMyMessage) 8.dp else 4.dp,
            shape = config.first,
            modifier = Modifier
                .widthIn(max = 280.dp, min = 60.dp)
                .align(config.second),
            color = if (isMyMessage) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}