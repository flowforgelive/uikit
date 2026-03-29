plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm()

    js {
        outputModuleName.set("uikit-common")
        browser()
        binaries.library()
        generateTypeScriptDefinitions()
    }

    sourceSets.all {
        languageSettings.optIn("kotlin.js.ExperimentalJsExport")
    }
}
