plugins {
	alias(libs.plugins.node.gradle)
}

node {
	download.set(false) // use system Node.js
}

val jsLibTask = ":core:uikit:common:jsBrowserProductionLibraryDistribution"
val sharedJsLibTask = ":apps:catalog-ui:shared:jsBrowserProductionLibraryDistribution"

val reactNpmInstall by tasks.registering(com.github.gradle.node.npm.task.NpmInstallTask::class) {
	dependsOn(jsLibTask)
	dependsOn(sharedJsLibTask)
	dependsOn(":core:uikit:react:npmInstall")
	workingDir.set(projectDir)
}

tasks.register<com.github.gradle.node.npm.task.NpmTask>("dev") {
	dependsOn(reactNpmInstall)
	workingDir.set(projectDir)
	npmCommand.set(listOf("run", "dev"))
}

tasks.register<com.github.gradle.node.npm.task.NpmTask>("build") {
	dependsOn(reactNpmInstall)
	workingDir.set(projectDir)
	npmCommand.set(listOf("run", "build"))
}

tasks.register<com.github.gradle.node.npm.task.NpmTask>("start") {
	dependsOn("build")
	workingDir.set(projectDir)
	npmCommand.set(listOf("run", "start"))
}
