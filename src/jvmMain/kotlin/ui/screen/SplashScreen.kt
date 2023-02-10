package ui.screen

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import ui.navigation.MainNavigationController
import ui.navigation.Screen
import ui.theme.buttonColor1

@Composable
fun SplashScreen() {
    var graphicLayer by remember { mutableStateOf(0f) }
    val controller = remember { MainNavigationController }
    LaunchedEffect(key1 = Unit, block = {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(2000)
        ) { value, _ ->
            graphicLayer = value
        }

    })

    LaunchedEffect(key1 = graphicLayer) {
        if (graphicLayer == 1f) {
            controller.navigate(Screen.Home)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.VideogameAsset,
            contentDescription = null,
            tint = buttonColor1,
            modifier = Modifier
                .size(240.dp)
                .graphicsLayer {
                    scaleX = graphicLayer
                    scaleY = graphicLayer
                }
        )
    }
}

