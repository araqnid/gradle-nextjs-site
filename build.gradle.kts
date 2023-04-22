plugins {
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.20"
    id("com.gradle.plugin-publish") version "1.2.0"
}

group = "org.araqnid.nextjs-site"
version = "0.0.1"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.github.node-gradle:gradle-node-plugin:3.6.0")

    testImplementation(kotlin("test-junit"))
    testImplementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.6.4"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
    testImplementation(platform("org.jetbrains.kotlinx:kotlinx-serialization-bom:1.5.0"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
}

gradlePlugin {
    vcsUrl.set("https://github.com/araqnid/gradle-nextjs-site")
    website.set("https://github.com/araqnid/gradle-nextjs-site")

    plugins {
        create("nextjsSitePlugin") {
            id = "org.araqnid.nextjs-site"
            displayName = "Next.JS site export"
            description = "Export a Next.JS site to a distributable archive"
            implementationClass = "org.araqnid.gradle.nextjssite.NextJsSitePlugin"
            tags.add("nodejs")
            tags.add("jest")
            tags.add("nextjs")
        }
    }
}
