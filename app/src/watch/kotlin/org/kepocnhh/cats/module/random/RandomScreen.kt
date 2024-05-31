package org.kepocnhh.cats.module.random

import android.graphics.BitmapFactory
import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kepocnhh.cats.App
import org.kepocnhh.cats.util.compose.SwipeToDismissBox
import java.io.File
import java.util.UUID

private fun save(bytes: ByteArray): File {
    val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    check(dir.exists())
    check(dir.isDirectory)
    check(dir.canRead())
    val file = dir.resolve("${UUID.randomUUID()}.jpg")
    file.writeBytes(bytes)
    return file
}

@Composable
private fun RandomScreen(
    bitmap: ImageBitmap,
    saved: File?,
    onSave: () -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        val scope: BoxWithConstraintsScope = this
        val padding = (scope.maxWidth * (kotlin.math.sqrt(2.0) - 1).toFloat()) / (2 * kotlin.math.sqrt(2.0).toFloat())
//        val padding = 48.dp
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = padding, bottom = 48.dp),
        ) {
//            Spacer(modifier = Modifier.height(padding))
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = padding),
                alignment = Alignment.TopCenter,
                bitmap = bitmap,
                contentDescription = "todo",
            )
            BasicText(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .wrapContentSize(),
                text = "${bitmap.width}x${bitmap.height}",
            )
            if (saved != null) {
                BasicText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = "Saved: ${saved.absolutePath}",
                )
            }
//            Spacer(modifier = Modifier.height(padding))
        }
        if (saved == null) {
            BasicText(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(onClick = onSave)
                    .wrapContentSize(),
                text = "save",
                style = TextStyle(color = Color.White),
            )
        }
    }
}

@Composable
private fun RandomScreen(bytes: ByteArray) {
    val bitmapState = remember { mutableStateOf<ImageBitmap?>(null) }
    val bitmap = bitmapState.value
    LaunchedEffect(Unit) {
        bitmapState.value = withContext(Dispatchers.Default) {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
        }
    }
    if (bitmap != null) {
        val savedState = remember { mutableStateOf<File?>(null) }
        val saving = remember { mutableStateOf(false) }
        LaunchedEffect(saving.value) {
            if (savedState.value == null && saving.value) {
                savedState.value = withContext(Dispatchers.Default) {
                    save(bytes)
                }
                saving.value = false
            }
        }
        RandomScreen(
            bitmap = bitmap,
            saved = savedState.value,
            onSave = {
                if (savedState.value == null && !saving.value) saving.value = true
            },
        )
    }
}

@Composable
private fun RandomScreen(
    error: Throwable,
    onTryAgain: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        BasicText(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.Center),
            text = "Loading a random cat error!",
            style = TextStyle(textAlign = TextAlign.Center),
        )
        BasicText(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(onClick = onTryAgain)
                .wrapContentSize(),
            text = "try again",
            style = TextStyle(color = Color.White),
        )
    }
}

@Composable
internal fun RandomScreen(
    onBack: () -> Unit,
) {
    SwipeToDismissBox(
        modifier = Modifier.fillMaxSize(),
        onDismissed = onBack,
    ) {
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
            val result = state.value?.result
            if (result == null) {
                BasicText(
                    modifier = Modifier.align(Alignment.Center),
                    text = "loading...",
                )
            } else {
                result.fold(
                    onSuccess = { bytes ->
                        RandomScreen(bytes = bytes)
                    },
                    onFailure = { error ->
                        println("Loading random cat error: $error") // todo
                        RandomScreen(
                            error = error,
                            onTryAgain = {
                                logics.requestRandomCat()
                            },
                        )
                    },
                )
            }
        }
    }
}
