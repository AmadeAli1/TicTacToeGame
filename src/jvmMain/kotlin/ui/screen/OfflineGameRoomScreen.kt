package ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import model.state.OfflineGameState
import ui.navigation.MainNavigationController
import ui.navigation.Screen
import ui.theme.Pink40
import ui.theme.color200
import ui.theme.firstCardColor
import ui.theme.mainColor
import ui.viewModel.OfflineGameViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OfflineGameScreen() {
    val controller = remember { MainNavigationController }
    val offlineViewModel = remember { OfflineGameViewModel() }
    val gameState = offlineViewModel.gameState
    val uiOfflineGameState by offlineViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = uiOfflineGameState) {
        if (uiOfflineGameState is OfflineGameState.CloseMatch) {
            controller.navigate(route = Screen.Home)
        }

        if (uiOfflineGameState is OfflineGameState.Failure) {
            (uiOfflineGameState as? OfflineGameState.Failure)?.message?.let { message ->
                snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
            }
        }

    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    TooltipArea(tooltip = { Text("Menu") }) {
                        Surface(
                            onClick = { controller.navigate(route = Screen.Home) },
                            shape = MaterialTheme.shapes.small,
                            shadowElevation = 4.dp,
                            modifier = Modifier.size(70.dp)
                                .padding(16.dp), color = firstCardColor
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu, contentDescription = null,
                                tint = color200
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(
                        alpha = 0.4f
                    )
                ),
            )
        },
        bottomBar = {
            BottomBar(round = gameState.round.value)
        }, snackbarHost = {
            snackbarHostState.currentSnackbarData?.let { data ->
                Snackbar(snackbarData = data, modifier = Modifier.fillMaxWidth())
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
            ) {

                Box(
                    modifier = Modifier.padding(vertical = 16.dp)
                        .align(Alignment.TopCenter)
                ) {
                    TopBar(
                        player1 = gameState.player1Details.value,
                        player2 = gameState.player2Details.value,
                        modifier = Modifier.fillMaxWidth(0.5f)
                    )
                }

                GameTable(tableState = gameState.tableState,
                    onGameAction = { action ->
                        offlineViewModel.sendGameAction(
                            action = action
                        )
                    }
                )

            }
        }

        if (uiOfflineGameState is OfflineGameState.ShowWinPopUp) {
            (uiOfflineGameState as OfflineGameState.ShowWinPopUp).player?.let { player ->
                GameWinDialog(
                    player = player,
                    onClose = offlineViewModel::closeMatch,
                    onContinue = offlineViewModel::nextMatch
                )
            }
        }

        if (uiOfflineGameState is OfflineGameState.Dialog) {
            CloseGameDialog(
                onDismiss = offlineViewModel::onCloseDialog,
                onNoClick = offlineViewModel::onCloseDialog,
                onYesClick = offlineViewModel::closeMatch
            )
        }

    }

}

@Composable
fun CloseGameDialog(onDismiss: () -> Unit, onNoClick: () -> Unit, onYesClick: () -> Unit) {
    Popup(onDismissRequest = onDismiss, alignment = Alignment.Center) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = mainColor),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp), verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Deseja sair?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.padding(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onNoClick,
                        shape = RoundedCornerShape(20),
                        contentPadding = PaddingValues(vertical = 10.dp),
                        modifier = Modifier.weight(0.5f)
                    ) {
                        Text(text = "No", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onYesClick,
                        shape = RoundedCornerShape(20),
                        contentPadding = PaddingValues(vertical = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Pink40),
                        modifier = Modifier.weight(0.5f)
                    ) {
                        Text(text = "Yes", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}