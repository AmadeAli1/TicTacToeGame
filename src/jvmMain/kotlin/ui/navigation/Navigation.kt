package ui.navigation

import androidx.compose.runtime.*

class Navigation<E>(
    start: E,
    private val destinations: List<E>,
) {
    private val screen: MutableState<E> = mutableStateOf(start)
    val route: State<E> = screen

    init {
        navigate(start)
    }

    private fun navigate(route: E) {
        if (destinations.contains(route)) {
            if (route != screen.value) {
                screen.value = route
            }
        } else {
            println("Destination Not found")
        }
    }

}

@Composable
fun <E> rememberNavigation(start: E, destinations: List<E>): Navigation<E> {
    return remember { Navigation(start, destinations) }
}

