package io.hugouwu.akt.assets

import io.hugouwu.akt.core.AssetKey
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import java.security.GeneralSecurityException

class AssetArchiveTest {
    @TempDir
    lateinit var temporaryDirectory: Path

    @Test
    fun packsAndReadsEncryptedAssets() {
        val source = temporaryDirectory.resolve("assets")
        Files.createDirectories(source.resolve("images"))
        Files.writeString(source.resolve("images/background.txt"), "night platform")
        val archive = temporaryDirectory.resolve("game.akt")

        AssetArchive.pack(
            sourceDirectory = source,
            archivePath = archive,
            options = ArchiveOptions(password = "test-password".toCharArray()),
        )

        val store = AssetArchive.open(archive, "test-password".toCharArray())
        assertEquals(setOf(AssetKey("images/background.txt")), store.keys)
        assertArrayEquals(
            "night platform".toByteArray(),
            store.read(AssetKey("images/background.txt")),
        )
    }

    @Test
    fun rejectsWrongPassword() {
        val source = temporaryDirectory.resolve("assets")
        Files.createDirectories(source)
        Files.writeString(source.resolve("script.txt"), "hello")
        val archive = temporaryDirectory.resolve("game.akt")

        AssetArchive.pack(source, archive, ArchiveOptions(password = "correct".toCharArray()))

        assertThrows<GeneralSecurityException> {
            AssetArchive.open(archive, "wrong".toCharArray())
        }
    }
}
