plugins {
    id("buildsrc.convention.kotlin-library")
}

dependencies {
    api(project(":akt-core"))
    api(project(":akt-ui"))
    api(project(":akt-assets"))
    api(project(":akt-render"))

    implementation(libs.slf4j.api)
    runtimeOnly(libs.logback.classic)
}
