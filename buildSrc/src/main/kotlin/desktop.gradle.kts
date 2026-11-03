package buildsrc.convention

import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("buildsrc.convention.kotlin-library")
}

val versionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
val lwjglVersion: String = versionCatalog.findVersion("lwjgl").get().requiredVersion
val operatingSystem: String = System.getProperty("os.name").lowercase()
val architecture: String = System.getProperty("os.arch").lowercase()

val nativesClassifier: String = when {
    operatingSystem.contains("win") -> "natives-windows"
    operatingSystem.contains("mac") && architecture.contains("aarch64") -> "natives-macos-arm64"
    operatingSystem.contains("mac") -> "natives-macos"
    architecture.contains("aarch64") -> "natives-linux-arm64"
    else -> "natives-linux"
}

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation(versionCatalog.findLibrary("lwjgl").get())
    implementation(versionCatalog.findLibrary("lwjgl-glfw").get())
    implementation(versionCatalog.findLibrary("lwjgl-vulkan").get())
    implementation(versionCatalog.findLibrary("lwjgl-stb").get())

    runtimeOnly("org.lwjgl:lwjgl::$nativesClassifier")
    runtimeOnly("org.lwjgl:lwjgl-glfw::$nativesClassifier")
    runtimeOnly("org.lwjgl:lwjgl-stb::$nativesClassifier")
}
