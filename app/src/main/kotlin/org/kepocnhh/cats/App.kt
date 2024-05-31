package org.kepocnhh.cats

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.Dispatchers
import org.kepocnhh.cats.module.app.Injection
import org.kepocnhh.cats.provider.Contexts
import org.kepocnhh.cats.provider.FinalRemotes
import sp.kx.logics.Logics
import sp.kx.logics.LogicsFactory
import sp.kx.logics.LogicsProvider
import sp.kx.logics.contains
import sp.kx.logics.get
import sp.kx.logics.remove

internal class App : Application() {
    override fun onCreate() {
        super.onCreate()
        _injection = Injection(
            contexts = Contexts(
                main = Dispatchers.Main,
                default = Dispatchers.Default,
            ),
            remotes = FinalRemotes(),
        )
    }

    companion object {
        private var _injection: Injection? = null

        private val _logicsProvider = LogicsProvider(
            factory = object : LogicsFactory {
                override fun <T : Logics> create(type: Class<T>): T {
                    val injection = checkNotNull(_injection) { "No injection!" }
                    return type
                        .getConstructor(Injection::class.java)
                        .newInstance(injection)
                }
            },
        )

        @Composable
        inline fun <reified T : Logics> logics(label: String = T::class.java.name): T {
            val (contains, logics) = synchronized(App::class.java) {
                remember { _logicsProvider.contains<T>(label = label) } to _logicsProvider.get<T>(label = label)
            }
            DisposableEffect(Unit) {
                onDispose {
                    if (!contains) _logicsProvider.remove<T>(label = label)
                }
            }
            return logics
        }
    }
}
