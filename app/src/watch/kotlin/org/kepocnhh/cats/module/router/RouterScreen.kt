package org.kepocnhh.cats.module.router

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.kepocnhh.cats.module.main.MainScreen
import org.kepocnhh.cats.module.random.RandomScreen

@Composable
internal fun RouterScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        val state = remember { mutableStateOf<MainScreen.State?>(null) }
        MainScreen(onState = {state.value = it})
        when (state.value) {
            MainScreen.State.Random -> RandomScreen(onBack = {state.value = null})
            null -> {
                // noop
            }
        }
    }
}
