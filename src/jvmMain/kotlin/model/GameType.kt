package model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.ui.graphics.vector.ImageVector

enum class GameType(val icon: ImageVector? = null) {
    X(Icons.Default.Clear),
    O(Icons.Default.TripOrigin),
    Empty
}