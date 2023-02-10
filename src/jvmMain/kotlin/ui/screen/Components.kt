package ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import model.GameType
import model.Player
import ui.theme.*
import validation.ValidationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Username(
    onUsernameChange: (String) -> Unit,
    username: String,
    validationState: ValidationState,
) {
    TextField(
        value = username,
        onValueChange = onUsernameChange,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Person, contentDescription = null
            )
        },
        supportingText = {
            if (validationState is ValidationState.Failure) {
                Text(text = validationState.message)
            }
        },
        placeholder = { Text(text = "Username", color = color200) },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = firstCardColor,
            textColor = color200,
            focusedIndicatorColor = Color.Unspecified,
            unfocusedIndicatorColor = Color.Unspecified,
            disabledIndicatorColor = Color.Unspecified,
            cursorColor = color200,
            errorIndicatorColor = Color.Unspecified
        ),
        isError = validationState is ValidationState.Failure,
        shape = RoundedCornerShape(10),
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomCode(
    modifier: Modifier = Modifier.fillMaxWidth(),
    onRoomChange: (String) -> Unit,
    room: String,
    validationState: ValidationState,
) {
    TextField(
        value = room,
        onValueChange = onRoomChange,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Tag, contentDescription = null
            )
        },
        supportingText = {
            if (validationState is ValidationState.Failure) {
                Text(text = validationState.message)
            }
        },
        placeholder = { Text(text = "Code", color = color200) },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = firstCardColor,
            textColor = color200,
            focusedIndicatorColor = Color.Unspecified,
            unfocusedIndicatorColor = Color.Unspecified,
            disabledIndicatorColor = Color.Unspecified,
            cursorColor = color200,
            errorIndicatorColor = Color.Unspecified
        ),
        isError = validationState is ValidationState.Failure,
        shape = RoundedCornerShape(10),
        modifier = modifier
    )
}


@Composable
fun GameInput(onClick: () -> Unit, state: Pair<String, GameType>) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(150.dp),
        shape = RoundedCornerShape(8),
        colors = ButtonDefaults.buttonColors(containerColor = firstCardColor),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        )
    ) {
        if (state.second != GameType.Empty) {
            Icon(
                imageVector = state.second.icon!!,
                contentDescription = null,
                tint = if (state.second == GameType.O) buttonColor1 else color200,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun GameWinDialog(player: Player, onClose: () -> Unit, onContinue: () -> Unit) {
    Popup(onDismissRequest = { /*TODO*/ }, alignment = Alignment.Center) {
        val winningColor = if (player.gameType == GameType.X) color200 else buttonColor1

        Card(
            modifier = Modifier
                .width(280.dp)
                .height(230.dp),
            colors = CardDefaults.cardColors(containerColor = mainColor),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp)
                    .animateContentSize(), verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Player Win",
                        style = MaterialTheme.typography.titleLarge.copy(color = winningColor),
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.VideogameAsset,
                        contentDescription = null,
                        tint = winningColor
                    )
                }
                Spacer(modifier = Modifier.padding(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = winningColor
                    )
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = winningColor
                        )
                    )
                }
                Spacer(modifier = Modifier.padding(6.dp))

                Text(
                    text = "Deseja continuar?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = color200
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onClose,
                        shape = RoundedCornerShape(20),
                        contentPadding = PaddingValues(vertical = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Pink40),
                        modifier = Modifier.weight(0.5f)
                    ) {
                        Text(text = "Close", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onContinue,
                        shape = RoundedCornerShape(20),
                        contentPadding = PaddingValues(vertical = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = winningColor),
                        modifier = Modifier.weight(0.5f)
                    ) {
                        Text(text = "Next Round", fontWeight = FontWeight.Bold)
                    }
                }

            }
        }
    }
}