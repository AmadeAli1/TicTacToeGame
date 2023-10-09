package ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import repository.GameRepository
import ui.navigation.MainNavigationController
import ui.navigation.Screen
import ui.theme.*

@Composable
fun HomeScreen() {
    val controller = remember { MainNavigationController }
    val repository = remember { GameRepository }
    val coroutineScope = rememberCoroutineScope()
    var isMatchMakingLoading by remember {
        mutableStateOf(false)
    }
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxHeight().fillMaxWidth(0.3f)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Body()
            Spacer(modifier = Modifier.padding(8.dp))
            GameMenuButton(
                onClick = {
                    controller.navigate(route = Screen.CreateRoom)
                },
                title = "Create Game",
                icon = Icons.Default.Gamepad
            )
            Spacer(modifier = Modifier.padding(5.dp))
            GameMenuButton(
                onClick = {
                    controller.navigate(route = Screen.JoinRoom)
                },
                title = "Join Game",
                containerColor = blueColor,
                contentColor = Color.White,
                icon = Icons.Default.People
            )
            Spacer(modifier = Modifier.padding(5.dp))
            GameMenuButton(
                enable = !isMatchMakingLoading,
                onClick = {
                    coroutineScope.launch {
                        repository.matchMaking(
                            onLoading = {
                                isMatchMakingLoading = true
                            },
                            onSuccess = {
                                isMatchMakingLoading = false
                                controller.navigate(route = Screen.GameRoom(it.gameId))
                            },
                            onFailure = {
                                isMatchMakingLoading = false
                                println(it)
                            }
                        )
                    }
                },
                title = "Matchmaking",
                containerColor = firstCardColor,
                contentColor = Color.White,
                icon = Icons.Default.PersonSearch
            )
            Spacer(modifier = Modifier.padding(5.dp))
            GameMenuButton(
                onClick = {
                    controller.navigate(route = Screen.Offline)
                },
                title = "Offline Game",
                containerColor = color200,
                icon = Icons.Default.VideogameAsset
            )

        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "Created by Amade Ali",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = buttonColor1
                )
            )
        }
    }
}

@Composable
fun GameMenuButton(
    onClick: () -> Unit,
    title: String,
    icon: ImageVector,
    containerColor: Color = buttonColor1,
    contentColor: Color = mainColor,
    enable: Boolean = true,
) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.extraSmall,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ), enabled = enable
    ) {
        Text(text = title, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.padding(horizontal = 2.dp))
        Icon(imageVector = icon, contentDescription = null)
    }
}

@Composable
private fun Body() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = firstCardColor,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "PICK PLAYER 1'S MARK",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Surface(
                shape = RoundedCornerShape(8),
                color = MaterialTheme.colorScheme.background
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource("ic_gametype_x.xml"),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground),
                        modifier = Modifier
                            .weight(0.5f)
                    )
                    Button(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(8.dp),
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(vertical = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TripOrigin, contentDescription = null,
                            modifier = Modifier.size(28.dp), tint = Color.Unspecified
                        )
                    }
                }
            }

            Text(
                text = "REMEMBER X: GOES FIRST",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

        }
    }
}