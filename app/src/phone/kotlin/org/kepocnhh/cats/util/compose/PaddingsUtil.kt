package org.kepocnhh.cats.util.compose

import android.view.WindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

internal fun WindowInsets.toPaddings(density: Float): PaddingValues {
    val insets = getInsets(WindowInsets.Type.systemBars())
    return PaddingValues(
        bottom = insets.bottom.px(density),
        end = insets.right.px(density),
        start = insets.left.px(density),
        top = insets.top.px(density),
    )
}

@Composable
internal fun WindowInsets.toPaddings(density: Density = LocalDensity.current): PaddingValues {
    return toPaddings(density = density.density)
}
