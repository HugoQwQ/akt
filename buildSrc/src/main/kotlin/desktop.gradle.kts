package buildsrc.convention

import org.gradle.api.artifacts.VersionCatalogsExtension

plugins { id("buildsrc.convention.kotlin-library") }

val versionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
val lwjglVersion: String = versionCatalog.findVersion("lwjgl").get().requiredVersion
val nativeClassifiers =
    listOf(
        "natives-windows",
        "natives-linux",
        "natives-linux-arm64",
        "natives-macos",
        "natives-macos-arm64",
    )

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation(versionCatalog.findLibrary("lwjgl").get())
    implementation(versionCatalog.findLibrary("lwjgl-glfw").get())
    implementation(versionCatalog.findLibrary("lwjgl-vulkan").get())
    implementation(versionCatalog.findLibrary("lwjgl-stb").get())

    nativeClassifiers.forEach { classifier ->
        runtimeOnly("org.lwjgl:lwjgl::$classifier")
        runtimeOnly("org.lwjgl:lwjgl-glfw::$classifier")
        runtimeOnly("org.lwjgl:lwjgl-stb::$classifier")
    }
}
