plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	jvm()

	js {
		outputModuleName.set("catalog-shared")
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
			implementation(project(":core:uikit:common"))
			implementation(libs.kotlinx.serialization.json)
		}
	}

	sourceSets.all {
		languageSettings.optIn("kotlin.js.ExperimentalJsExport")
	}
}
