package org.kepocnhh.cats.module.random

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kepocnhh.cats.App
import org.kepocnhh.cats.util.compose.BackHandler

@Composable
private fun RandomScreen(bitmap: ImageBitmap) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.Center),
            bitmap = bitmap,
            contentDescription = "todo",
        )
    }
}

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
        val logics = App.logics<RandomLogics>()
        val state = logics.state.collectAsState()
        LaunchedEffect(Unit) {
            if (state.value == null) {
                logics.requestRandomCat()
            }
        }
        val bytes = state.value?.bytes
        val bitmapState = remember { mutableStateOf<ImageBitmap?>(null) }
        val bitmap = bitmapState.value
        LaunchedEffect(bytes) {
            if (bytes != null && bitmap == null) {
                withContext(Dispatchers.Default) {
                    bitmapState.value = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
                }
            }
        }
        if (bitmap == null) {
            BasicText(
                modifier = Modifier.align(Alignment.Center),
                text = "loading...",
            )
        } else {
            RandomScreen(bitmap = bitmap)
        }
    }
}
