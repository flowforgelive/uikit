plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.compose.multiplatform)
	alias(libs.plugins.kotlin.compose)
}

kotlin {
	jvm()

	sourceSets {
		commonMain.dependencies {
			implementation(project(":core:uikit:common"))
			implementation(project(":core:uikit:compose"))
			implementation(project(":apps:catalog-ui:shared"))
			implementation(compose.material3)
			implementation(compose.materialIconsExtended)
			implementation(compose.foundation)
			implementation(compose.runtime)
		}
		jvmMain.dependencies {
			implementation(compose.desktop.currentOs)
		}
	}
}

compose.desktop {
	application {
		mainClass = "MainKt"

		nativeDistributions {
			packageName = "UIKit Catalog"
			packageVersion = "1.0.0"

			macOS {
				bundleID = "com.uikit.catalog"
			}
		}
	}
}
