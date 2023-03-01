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

        target.tasks.register("nextBuild", YarnTask::class.java) { task ->
            val extension = task.project.extensions.getByType(NextJsSiteExtension::class.java)
            task.description = "Build Next.JS server"
            task.inputs.dir("src")
            task.inputs.file("next.config.js")
            task.inputs.file("package.json")
            task.inputs.file("yarn.lock")
            task.inputs.property("debugBuild", extension.debugBuild)
            task.inputs.property("productionProfiling", extension.productionProfiling)
            task.inputs.property("lint", extension.lint)
            task.outputs.dir(".next")
            task.dependsOn("yarn")
            task.args.set(listOf("next", "build"))
            task.args.addAll(extension.debugBuild.map { if (it) listOf("--debug") else emptyList() })
            task.args.addAll(extension.productionProfiling.map { if (it) listOf("--profile") else emptyList() })
            task.args.addAll(extension.lint.map { if (it) emptyList() else listOf("--no-lint") })
        }

        target.tasks.register("nextExport", YarnTask::class.java) { task ->
            val siteDir = task.project.layout.buildDirectory.dir("site")
            task.description = "Export Next.js pages to static files"
            task.inputs.dir(".next")
            task.inputs.file("next.config.js")
            task.inputs.dir("public")
            task.outputs.dir(siteDir)
            task.dependsOn("yarn", "nextBuild")
            task.args.set(siteDir.map { listOf("next", "export", "-o", it.toString()) })
        }

        target.tasks.register("jestTest", YarnTask::class.java) { task ->
            val taskOutputDir = task.project.layout.buildDirectory.dir("test-results").map { it.dir(task.name) }
            task.group = "verification"
            task.description = "Run Javascript tests using Jest on nodejs"
            task.inputs.dir("src")
            task.inputs.dir("test")
            task.inputs.file("package.json")
            task.inputs.file("yarn.lock")
            task.inputs.files(task.project.fileTree(task.project.projectDir) {
                it.include("jest.*.js") // typically jest.config.js, jest.setup.js
            })
            task.outputs.dir(taskOutputDir)
            task.dependsOn("yarn")
            task.args.set(listOf("jest", "--ci", "--reporters=default", "--reporters=jest-junit"))
            task.environment.put("JEST_JUNIT_OUTPUT_DIR", taskOutputDir.map { it.toString() })
            task.environment.put("JEST_JUNIT_OUTPUT_NAME", "UI-jest-node.xml")
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
