package buildsrc.convention

import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar

plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("org.jetbrains.dokka")
    `java-library`
    `maven-publish`
}

group = rootProject.group

version = rootProject.version

java { withSourcesJar() }

val javadocJar =
    tasks.register<Jar>("javadocJar") {
        group = "documentation"
        description = "packages generated api documentation"
        dependsOn(tasks.named("dokkaGenerateHtml"))
        from(layout.buildDirectory.dir("dokka/html"))
        archiveClassifier.set("javadoc")
    }

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(javadocJar)
            pom {
                name.set(project.name)
                description.set("${project.name} module for akt, a visual novel framework.")
            }
        }
    }
    repositories {
        maven {
            name = "staging"
            url = uri(rootProject.layout.buildDirectory.dir("pages/maven").get().asFile)
        }
    }
}
