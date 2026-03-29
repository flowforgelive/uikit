plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    jvm()

    sourceSets {
        jvmMain.dependencies {
            implementation(project(":core:uikit:common"))
            implementation(project(":core:uikit:compose"))
            implementation(compose.desktop.currentOs)
            implementation(compose.material3)
            implementation(compose.foundation)
            implementation(compose.runtime)
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}
