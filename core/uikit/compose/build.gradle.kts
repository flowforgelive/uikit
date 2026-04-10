plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.compose.multiplatform)
	alias(libs.plugins.kotlin.compose)
}

kotlin {
	jvm()

	sourceSets {
		commonMain.dependencies {
			api(project(":core:uikit:common"))
			implementation(compose.material3)
			implementation(compose.foundation)
			implementation(compose.runtime)
			api(libs.haze)
		}
		jvmMain.dependencies {
			implementation(compose.desktop.common)
		}
	}
}
