plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	jvm()

	js {
		outputModuleName.set("uikit-common")
		useEsModules()
		browser()
		binaries.library()
		generateTypeScriptDefinitions()
		compilerOptions {
			moduleKind.set(org.jetbrains.kotlin.gradle.dsl.JsModuleKind.MODULE_ES)
		}
	}

	sourceSets {
		commonMain.dependencies {
			implementation(libs.kotlinx.serialization.json)
			implementation(libs.kotlinx.coroutines.core)
		}
		commonTest.dependencies {
			implementation(kotlin("test"))
		}
	}

	sourceSets.all {
		languageSettings.optIn("kotlin.js.ExperimentalJsExport")
	}
}
