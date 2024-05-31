package org.kepocnhh.cats.module.random

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.kepocnhh.cats.util.compose.BackHandler

@Composable
internal fun RandomScreen(
    onBack: () -> Unit,
) {
    BackHandler(block = onBack)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        // todo
    }
}
