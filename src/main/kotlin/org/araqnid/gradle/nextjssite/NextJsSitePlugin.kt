package org.araqnid.gradle.nextjssite

import com.github.gradle.node.yarn.task.YarnTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete

@Suppress("unused")
class NextJsSitePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.pluginManager.apply("base")
        target.pluginManager.apply("com.github.node-gradle.node")

        target.extensions.create("nextJsSite", NextJsSiteExtension::class.java)

        target.configurations.create("web") { cfg ->
            cfg.isCanBeResolved = false
        }

        target.tasks.named("clean", Delete::class.java).configure { task ->
            task.delete("node_modules")
            task.delete(".next")
        }

        target.tasks.register<YarnTask>("nextBuild") {
            description = "Build Next.JS server"
            inputs.dir("src")
            inputs.file("next.config.js")
            inputs.file("package.json")
            inputs.file("yarn.lock")
            inputs.property("debugBuild", project.nextJsSiteExtension.debugBuild)
            inputs.property("productionProfiling", project.nextJsSiteExtension.productionProfiling)
            inputs.property("lint", project.nextJsSiteExtension.lint)
            outputs.dir(".next")
            dependsOn("yarn")
            args.set(listOf("next", "build"))
            args.addFrom(project.nextJsSiteExtension.debugBuild) { if (it) add("--debug") }
            args.addFrom(project.nextJsSiteExtension.productionProfiling) { if (it) add("--profile") }
            args.addFrom(project.nextJsSiteExtension.lint) { if (!it) add("--no-lint") }
        }

        target.tasks.register<YarnTask>("nextExport") {
            val siteDir = project.layout.buildDirectory.dir("site")
            description = "Export Next.js pages to static files"
            inputs.dir(".next")
            inputs.file("next.config.js")
            inputs.files("public")
            outputs.dir(siteDir)
            dependsOn("yarn", "nextBuild")
            args.addFrom(siteDir) {
                add("next")
                add("export")
                add("-o")
                add(it.toString())
            }
        }

        target.tasks.register<YarnTask>("jestTest") {
            val taskOutputDir = project.layout.buildDirectory.dir("test-results").map { it.dir(name) }
            group = "verification"
            description = "Run Javascript tests using Jest on nodejs"
            inputs.dir("src")
            inputs.files("test")
            inputs.file("package.json")
            inputs.file("yarn.lock")
            inputs.files(project.fileTree(project.projectDir) {
                it.include("jest.*.js") // typically jest.config.js, jest.setup.js
            })
            outputs.dir(taskOutputDir)
            dependsOn("yarn")
            args.set(listOf("jest", "--ci", "--reporters=default", "--reporters=jest-junit", "--passWithNoTests"))
            environment.put("JEST_JUNIT_OUTPUT_DIR", taskOutputDir.map { it.toString() })
            environment.put("JEST_JUNIT_OUTPUT_NAME", "UI-jest-node.xml")
        }

        target.tasks.named("assemble").configure { task ->
            task.dependsOn("nextExport")
        }

        target.tasks.named("check").configure { task ->
            task.dependsOn("jestTest")
        }

        target.dependencies.add("web", target.files(target.tasks.named("nextExport")))
    }
}

private val Project.nextJsSiteExtension: NextJsSiteExtension
    get() = extensions.getByType(NextJsSiteExtension::class.java)
