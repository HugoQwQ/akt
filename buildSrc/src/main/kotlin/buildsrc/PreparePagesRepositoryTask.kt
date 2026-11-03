package buildsrc

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class PreparePagesRepositoryTask : DefaultTask() {
    @get:OutputFile abstract val markerFile: RegularFileProperty

    @TaskAction
    fun prepare() {
        val marker = markerFile.get().asFile
        marker.parentFile.mkdirs()
        marker.writeText("")
    }
}
