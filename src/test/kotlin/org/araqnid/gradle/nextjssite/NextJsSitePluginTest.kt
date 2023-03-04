package org.araqnid.gradle.nextjssite

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import org.araqnid.gradle.kotlin.nodejsapplication.TestProjectDirectory
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertContains

class NextJsSitePluginTest {
    @get:Rule
    val testProjectDir = TestProjectDirectory()

    @Test
    fun `produces site`() {
        testProjectDir.path.resolve("build.gradle.kts").writeText(
            """
                plugins {
                  id("org.araqnid.nextjs-site")
                }
                
                nextJsSite {
                }
            """.trimIndent()
        )

        testProjectDir.path.resolve("package.json").writeText(Json.encodeToString(buildJsonObject {
            putJsonObject("scripts") {
                put("build", "next build")
                put("export", "next export")
                put("test", "jest")
            }
            putJsonObject("dependencies") {
                put("next", "^13.2.1")
                put("react", "^18.0.0")
                put("react-dom", "^18.0.0")
            }
            putJsonObject("devDependencies") {
                put("jest", "^27.5.1")
                put("jest-junit", "^13.0.0")
            }
        }))

        testProjectDir.path.resolve("next.config.js").writeText(
            """
            module.exports = {
              trailingSlash: true,
            }
        """.trimIndent()
        )

        testProjectDir.path.resolve("src/pages/index.jsx").mkdirs().writeText(
            """
            import React from 'react'
            
            export default function IndexPage() {
                return (
                    <main>hello world</main>
                )
            }
        """.trimIndent()
        )

        GradleRunner.create()
            .withProjectDir(testProjectDir.path.toFile())
            .withArguments("build")
            .withPluginClasspath()
            .build()

        assertContains(testProjectDir.path.resolve("build/site/index.html").readText(), "<main>hello world</main>")
        assertContains(
            testProjectDir.path.resolve("build/test-results/jestTest/UI-jest-node.xml").readText(),
            "<testsuites name=\"jest tests\""
        )
    }

    private fun Path.mkdirs(): Path = apply {
        Files.createDirectories(parent)
    }
}