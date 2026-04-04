pluginManagement {
	repositories {
		gradlePluginPortal()
		google()
		mavenCentral()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	}
}

rootProject.name = "uikit"

include(":core:uikit:common")
include(":core:uikit:compose")
include(":core:uikit:react")
include(":apps:catalog-ui:shared")
include(":apps:catalog-ui:compose")
include(":apps:catalog-ui:react")
