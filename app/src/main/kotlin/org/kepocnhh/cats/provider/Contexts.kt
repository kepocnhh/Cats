package org.kepocnhh.cats.provider

import kotlin.coroutines.CoroutineContext

internal data class Contexts(
    val main: CoroutineContext,
    val default: CoroutineContext,
)
