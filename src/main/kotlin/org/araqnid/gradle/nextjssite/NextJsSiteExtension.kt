package org.araqnid.gradle.nextjssite

import org.gradle.api.provider.Property

@Suppress("LeakingThis")
abstract class NextJsSiteExtension {
    abstract val debugBuild: Property<Boolean>
    abstract val productionProfiling: Property<Boolean>
    abstract val lint: Property<Boolean>

    init {
        debugBuild.convention(false)
        productionProfiling.convention(false)
        lint.convention(true)
    }
}
