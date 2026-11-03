plugins {
    id("buildsrc.convention.kotlin-application")
}

dependencies {
    implementation(project(":akt-runtime"))
}

application {
    mainClass = "io.hugouwu.akt.example.ExampleGameKt"
}
