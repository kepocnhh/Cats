package org.kepocnhh.cats.module.app

import org.kepocnhh.cats.provider.Contexts
import org.kepocnhh.cats.provider.Remotes

internal data class Injection(
    val contexts: Contexts,
    val remotes: Remotes,
)
