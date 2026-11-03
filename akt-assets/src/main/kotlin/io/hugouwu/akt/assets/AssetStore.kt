package io.hugouwu.akt.assets

import io.hugouwu.akt.core.AssetKey
import java.nio.file.Files
import java.nio.file.Path

public interface AssetStore {
    public fun read(key: AssetKey): ByteArray?

    public fun contains(key: AssetKey): Boolean = read(key) != null

    public companion object {
        public val empty: AssetStore = object : AssetStore {
            override fun read(key: AssetKey): ByteArray? = null
        }
    }
}

public class DirectoryAssetStore(
    root: Path,
) : AssetStore {
    private val root: Path = root.toAbsolutePath().normalize()

    init {
        require(Files.isDirectory(this.root)) { "asset directory does not exist: ${this.root}" }
    }

    override fun read(key: AssetKey): ByteArray? {
        val target = this.root.resolve(key.value).normalize()
        require(target.startsWith(this.root)) { "asset key escapes the asset directory" }
        return if (Files.isRegularFile(target)) Files.readAllBytes(target) else null
    }
}
