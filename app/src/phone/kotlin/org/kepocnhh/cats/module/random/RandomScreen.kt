package org.kepocnhh.cats.module.random

import android.graphics.BitmapFactory
import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kepocnhh.cats.App
import org.kepocnhh.cats.util.compose.BackHandler
import org.kepocnhh.cats.util.compose.toPaddings
import java.io.File
import java.util.UUID

@Composable
private fun RandomScreen(
    bitmap: ImageBitmap,
    saved: File?,
    onSave: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        val insets = LocalView.current.rootWindowInsets.toPaddings()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(insets.calculateTopPadding()))
            Image(
                modifier = Modifier
                    .fillMaxWidth(),
                alignment = Alignment.TopCenter,
                bitmap = bitmap,
                contentDescription = "todo",
            )
            Spacer(modifier = Modifier.height(insets.calculateBottomPadding()))
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
            ) {
                BasicText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.25f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .wrapContentHeight(),
                    text = "Size: ${bitmap.width}x${bitmap.height}",
                )
                if (saved == null) {
                    BasicText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(Color.Black.copy(alpha = 0.25f))
                            .clickable(enabled = saved == null, onClick = onSave)
                            .padding(start = 16.dp)
                            .wrapContentSize(),
                        text = "Save",
                    )
                } else {
                    BasicText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.25f))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .wrapContentHeight(),
                        text = "Saved: ${saved.absolutePath}",
                    )
                }
            }
        }
    }
}

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
private fun RandomScreen(bytes: ByteArray) {
    val bitmapState = remember { mutableStateOf<ImageBitmap?>(null) }
    val bitmap = bitmapState.value
    LaunchedEffect(bytes) {
        if (bitmap == null) {
            withContext(Dispatchers.Default) {
                bitmapState.value = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
            }
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
        ) {
            BasicText(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .wrapContentSize(),
                text = "Loading a random cat error!",
            )
            BasicText(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clickable(onClick = onTryAgain)
                    .wrapContentSize(),
                text = "try again",
            )
        }
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
