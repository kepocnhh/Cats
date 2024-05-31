package org.kepocnhh.cats.module.router

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import org.kepocnhh.cats.module.main.MainScreen
import org.kepocnhh.cats.module.random.RandomScreen
import org.kepocnhh.cats.util.compose.LocalOnBackPressedDispatcher

@Composable
private fun RouterScreen(
    state: MainScreen.State?,
    onState: (MainScreen.State) -> Unit,
    onMain: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        when (state) {
            MainScreen.State.Random -> RandomScreen(onBack = onMain)
            null -> MainScreen(onState = onState)
        }
    }
}

@Composable
internal fun RouterScreen() {
    val activity = LocalContext.current as? ComponentActivity ?: TODO()
    CompositionLocalProvider(
        LocalOnBackPressedDispatcher provides activity.onBackPressedDispatcher,
    ) {
        val state = remember { mutableStateOf<MainScreen.State?>(null) }
        RouterScreen(
            state = state.value,
            onState = {state.value = it},
            onMain = {state.value = null},
        )
    }
}
