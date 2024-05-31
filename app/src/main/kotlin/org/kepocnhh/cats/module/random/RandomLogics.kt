package org.kepocnhh.cats.module.random

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.kepocnhh.cats.module.app.Injection
import sp.kx.logics.Logics

internal class RandomLogics(
    private val injection: Injection,
) : Logics(injection.contexts.main) {
    class State(
        val bytes: ByteArray,
    )

    private val _state = MutableStateFlow<State?>(null)
    val state = _state.asStateFlow()

    fun requestRandomCat() = launch {
        val bytes = withContext(injection.contexts.default) {
            injection.remotes.getRandomCat()
        }
        _state.value = State(bytes = bytes)
    }
}
