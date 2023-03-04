package org.araqnid.gradle.nextjssite

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider

internal fun <E, P> ListProperty<in E>.addFrom(
    provider: Provider<out P>,
    convert: suspend SequenceScope<E>.(P) -> Unit
) {
    addAll(provider.map { value ->
        val seq = sequence {
            convert(value)
        }
        seq.toList()
    })
}
