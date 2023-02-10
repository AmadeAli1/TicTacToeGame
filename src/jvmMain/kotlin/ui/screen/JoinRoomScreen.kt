package ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import model.state.GameRoomState
import ui.navigation.MainNavigationController
import ui.navigation.Screen
import ui.theme.firstCardColor
import ui.viewModel.JoinGameRoomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinRoomScreen() {
    val viewModel = remember { JoinGameRoomViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    val controller = remember { MainNavigationController }


    LaunchedEffect(key1 = uiState) {
        when (uiState) {

            is GameRoomState.Success -> {
                val gameId = (uiState as GameRoomState.Success).gameId
                controller.navigate(route = Screen.GameRoom(id = gameId))
            }

            else -> Unit
        }
    }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Join a game",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                        )
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = { controller.navigate(route = Screen.Home) }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background.copy(
                    alpha = 0.4f
                )
            ),
        )
    }) {
        Box(
            modifier = Modifier.fillMaxSize().padding(it),
            contentAlignment = Alignment.Center
        ) {
            if (uiState is GameRoomState.Failure) {
                Popup(alignment = Alignment.TopCenter, onDismissRequest = {}) {
                    (uiState as? GameRoomState.Failure)?.message?.let {
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
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.35f)
                    .padding(16.dp)
                    .animateContentSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Username(
                    onUsernameChange = viewModel::onUsernameChange,
                    username = viewModel.username.value,
                    validationState = viewModel.usernameValidation.collectAsState().value
                )
                Spacer(modifier = Modifier.padding(4.dp))

                RoomCode(
                    onRoomChange = viewModel::onRoomChange,
                    room = viewModel.room.value,
                    validationState = viewModel.roomValidation.collectAsState().value
                )
                Spacer(modifier = Modifier.padding(8.dp))

                GameMenuButton(
                    onClick = viewModel::join,
                    title = "Join",
                    icon = Icons.Default.NavigateNext
                )

                Spacer(modifier = Modifier.padding(6.dp))
                if (uiState is GameRoomState.Loading) {
                    CircularProgressIndicator()
                }


            }
        }

    }


}