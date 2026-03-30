plugins {
	alias(libs.plugins.node.gradle)
}

node {
	download.set(false)
}

val jsLibTask = ":core:uikit:common:jsBrowserProductionLibraryDistribution"

tasks.named("npmInstall") {
	dependsOn(jsLibTask)
}
