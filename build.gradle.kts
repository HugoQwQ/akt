import buildsrc.PreparePagesRepositoryTask

plugins {
    base
    id("com.ncorti.ktfmt.gradle")
}

ktfmt {
    kotlinLangStyle()
    maxWidth.set(100)
    removeUnusedImports.set(true)
}

tasks.register("apiDocs") {
    group = "documentation"
    description = "generates api documentation for every library module"
    dependsOn(
        ":akt-core:dokkaGenerateHtml",
        ":akt-assets:dokkaGenerateHtml",
        ":akt-ui:dokkaGenerateHtml",
        ":akt-render:dokkaGenerateHtml",
        ":akt-runtime:dokkaGenerateHtml",
    )
}

val publishableModules = listOf("akt-core", "akt-assets", "akt-ui", "akt-render", "akt-runtime")

tasks.register("publishRepository") {
    group = "publishing"
    description = "publishes all library modules to the staged maven repository"
    dependsOn(
        publishableModules.map { module -> ":$module:publishMavenPublicationToStagingRepository" }
    )
}

tasks.register<PreparePagesRepositoryTask>("preparePagesRepository") {
    group = "publishing"
    description = "adds the github pages marker to the staged maven repository"
    dependsOn("publishRepository")
    markerFile.set(layout.buildDirectory.file("pages/.nojekyll"))
}

tasks.register("release") {
    group = "build"
    description = "runs the release build and stages the maven repository"
    dependsOn("apiDocs")
    dependsOn("preparePagesRepository")
    dependsOn(
        ":akt-core:build",
        ":akt-assets:build",
        ":akt-ui:build",
        ":akt-render:build",
        ":akt-runtime:build",
        ":example:build",
    )
}
