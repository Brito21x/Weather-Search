// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

allprojects {
    val tempDir = System.getProperty("java.io.tmpdir")
    layout.buildDirectory.set(file("$tempDir/WeatherSearchBuild/${project.name}"))
}
