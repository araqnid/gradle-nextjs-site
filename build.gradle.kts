plugins {
    kotlin("jvm") version "1.8.10"
    id("com.gradle.plugin-publish") version "1.1.0"
}

group = "org.araqnid.gradle"
version = "0.0.1"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.github.node-gradle:gradle-node-plugin:3.5.1")
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
        }
    }
}
