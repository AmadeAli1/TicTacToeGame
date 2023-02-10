package ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import model.GameType
import model.Player
import model.RoomState
import model.state.UiState
import ui.navigation.MainNavigationController
import ui.navigation.Screen
import ui.theme.blueColor
import ui.theme.buttonColor1
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

    LaunchedEffect(key1 = roomState) {
        if (roomState == RoomState.Closed || roomState == RoomState.ExpiredSeason) {
            controller.navigate(route = Screen.Home)
        }
    }
    Scaffold(
        topBar = {
            gameState?.let {
                if (it.player2 != null) {
                    TopBar(player1 = it.player1, player2 = it.player2!!)
                }
            }
        }, bottomBar = {
            gameState?.matchState?.let {
                BottomBar(round = it.round)
            }
        }
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
fun TopBar(player1: Player, player2: Player) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
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
                    text = player1.name,
                    style = MaterialTheme.typography.titleMedium.copy(color = blueColor)
                )
            }
        }

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
                    text = player2.name,
                    style = MaterialTheme.typography.titleMedium.copy(color = buttonColor1)
                )
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