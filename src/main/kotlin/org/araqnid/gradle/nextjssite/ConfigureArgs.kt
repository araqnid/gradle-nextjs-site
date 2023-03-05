package org.araqnid.gradle.nextjssite

import org.gradle.api.provider.HasMultipleValues
import org.gradle.api.provider.Provider

internal fun <E : Any, P> HasMultipleValues<in E>.addFrom(
    provider: Provider<out P>,
    convert: MutableList<E>.(P) -> Unit
) {
    addAll(provider.map { value ->
        mutableListOf<E>().apply { convert(value) }
    })
}
