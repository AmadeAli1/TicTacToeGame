package ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val darkScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = mainColor,
    onBackground = color200
)

private val lightScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = mainColor,
    onBackground = color200
)

@Composable
fun GameTheme(
    shapes: Shapes = MaterialTheme.shapes,
    typography: Typography = MaterialTheme.typography,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (isSystemInDarkTheme()) darkScheme else lightScheme
    MaterialTheme(
        colorScheme = colorScheme, shapes = shapes, typography = typography,
        content = content,
    )
}