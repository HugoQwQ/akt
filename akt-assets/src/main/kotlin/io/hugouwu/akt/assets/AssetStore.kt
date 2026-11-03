package io.hugouwu.akt.assets

import io.hugouwu.akt.core.AssetKey
import java.nio.file.Files
import java.nio.file.Path

/** read-only source for assets addressed by [AssetKey]. */
public interface AssetStore {
    /** returns asset bytes, or null when [key] is unavailable. */
    public fun read(key: AssetKey): ByteArray?

    /** returns true when [key] can be read from this store. */
    public fun contains(key: AssetKey): Boolean = read(key) != null

    /** shared empty stores. */
    public companion object {
        /** a store that contains no assets. */
        public val empty: AssetStore =
            object : AssetStore {
                override fun read(key: AssetKey): ByteArray? = null
            }
    }
}

/**
 * reads assets directly from files below [root].
 *
 * @param root existing asset directory.
 * @throws IllegalArgumentException when [root] is not an existing directory.
 */
public class DirectoryAssetStore(root: Path) : AssetStore {
    private val root: Path = root.toAbsolutePath().normalize()

    init {
        require(Files.isDirectory(this.root)) { "asset directory does not exist: ${this.root}" }
    }

    /**
     * reads an asset below [root].
     *
     * @throws IllegalArgumentException when [key] escapes [root].
     */
    override fun read(key: AssetKey): ByteArray? {
        val target = this.root.resolve(key.value).normalize()
        require(target.startsWith(this.root)) { "asset key escapes the asset directory" }
        return if (Files.isRegularFile(target)) Files.readAllBytes(target) else null
    }
}
