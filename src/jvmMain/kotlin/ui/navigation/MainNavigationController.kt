package ui.navigation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object MainNavigationController {
    private val navigationScope = CoroutineScope(Dispatchers.IO)

    private val screenMutableStateFlow = MutableStateFlow<Screen>(Screen.Splash)
    val screen = screenMutableStateFlow.asStateFlow()

    fun navigate(route: Screen) {
        navigationScope.launch {
            if (screen.value != route) {
                screenMutableStateFlow.emit(route)
            }
        }
    }

}