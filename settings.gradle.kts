dependencyResolutionManagement { @Suppress("UnstableApiUsage") repositories { mavenCentral() } }

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }

include(
    ":akt-core",
    ":akt-assets",
    ":akt-ui",
    ":akt-render",
    ":akt-runtime",
    ":example",
)

rootProject.name = "akt"
