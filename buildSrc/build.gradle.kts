plugins {
    `kotlin-dsl`
    alias(libs.plugins.ktfmtGradle)
}

ktfmt {
    kotlinLangStyle()
    maxWidth.set(100)
    removeUnusedImports.set(true)
}

kotlin { jvmToolchain(21) }

dependencies {
    implementation(libs.kotlinGradlePlugin)
    implementation(libs.dokkaGradlePlugin)
    implementation(libs.ktfmtGradlePlugin)
}
