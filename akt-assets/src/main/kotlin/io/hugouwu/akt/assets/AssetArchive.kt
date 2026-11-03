package io.hugouwu.akt.assets

import io.hugouwu.akt.core.AssetKey
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.security.GeneralSecurityException
import java.security.SecureRandom
import java.util.Collections
import java.util.zip.Deflater
import java.util.zip.Inflater
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.path.relativeTo

public data class ArchiveOptions(
    public val compressionLevel: Int = Deflater.BEST_COMPRESSION,
    public val password: CharArray? = null,
)

public object AssetArchive {
    private const val magic: Int = 0x414b5431
    private const val version: Int = 1
    private const val encryptedFlag: Int = 1
    private const val saltLength: Int = 16
    private const val nonceLength: Int = 12
    private const val tagLengthBits: Int = 128
    private const val keyLengthBits: Int = 256
    private const val defaultIterations: Int = 210_000
    private val random: SecureRandom = SecureRandom()

    public fun pack(
        sourceDirectory: Path,
        archivePath: Path,
        options: ArchiveOptions = ArchiveOptions(),
    ) {
        require(Files.isDirectory(sourceDirectory)) { "asset source directory does not exist" }
        require(options.compressionLevel in Deflater.BEST_SPEED..Deflater.BEST_COMPRESSION) {
            "invalid compression level"
        }
        val password = options.password
        require(password == null || password.isNotEmpty()) { "password cannot be empty" }

        val root = sourceDirectory.toAbsolutePath().normalize()
        val files = Files.walk(root).use { paths ->
            paths
                .filter(Files::isRegularFile)
                .sorted()
                .toList()
        }

        val entryPayload = ByteArrayOutputStream()
        DataOutputStream(entryPayload).use { output ->
            output.writeInt(files.size)
            files.forEach { file ->
                val key = file.relativeTo(root).toString().replace('\\', '/')
                val raw = Files.readAllBytes(file)
                val compressed = compress(raw, options.compressionLevel)
                output.writeUTF(key)
                output.writeInt(raw.size)
                output.writeInt(compressed.size)
                output.write(compressed)
            }
        }

        val salt = ByteArray(saltLength).also(random::nextBytes)
        val nonce = ByteArray(nonceLength).also(random::nextBytes)
        val flags = if (password == null) 0 else encryptedFlag
        val body = if (password == null) {
            entryPayload.toByteArray()
        } else {
            encrypt(entryPayload.toByteArray(), password, salt, nonce)
        }

        Files.createDirectories(archivePath.toAbsolutePath().parent)
        Files.newOutputStream(archivePath).use { stream ->
            DataOutputStream(stream).use { output ->
                output.writeInt(magic)
                output.writeInt(version)
                output.writeByte(flags)
                output.writeInt(defaultIterations)
                output.write(salt)
                output.write(nonce)
                output.writeInt(body.size)
                output.write(body)
            }
        }
    }

    public fun open(
        archivePath: Path,
        password: CharArray? = null,
    ): ArchiveAssetStore {
        val archiveBytes = Files.readAllBytes(archivePath)
        DataInputStream(ByteArrayInputStream(archiveBytes)).use { input ->
            require(input.readInt() == magic) { "file is not an akt archive" }
            require(input.readInt() == version) { "unsupported akt archive version" }
            val flags = input.readUnsignedByte()
            require(flags and encryptedFlag.inv() == 0) { "unknown akt archive flags" }
            val iterations = input.readInt()
            val salt = input.readNBytes(saltLength)
            val nonce = input.readNBytes(nonceLength)
            val bodyLength = input.readInt()
            require(bodyLength >= 0) { "invalid archive body length" }
            val body = input.readNBytes(bodyLength)
            require(body.size == bodyLength) { "akt archive is truncated" }

            val payload = if (flags and encryptedFlag != 0) {
                require(password != null && password.isNotEmpty()) {
                    "this akt archive requires a password"
                }
                decrypt(body, password, salt, nonce, iterations)
            } else {
                require(password == null) { "an unencrypted akt archive does not use a password" }
                body
            }
            return ArchiveAssetStore(readEntries(payload))
        }
    }

    private fun readEntries(payload: ByteArray): Map<AssetKey, ByteArray> {
        val entries = linkedMapOf<AssetKey, ByteArray>()
        DataInputStream(ByteArrayInputStream(payload)).use { input ->
            val count = input.readInt()
            require(count >= 0) { "invalid archive entry count" }
            repeat(count) {
                val key = AssetKey(input.readUTF())
                val originalSize = input.readInt()
                val compressedSize = input.readInt()
                require(originalSize >= 0 && compressedSize >= 0) {
                    "invalid archive entry size"
                }
                val compressed = input.readNBytes(compressedSize)
                require(compressed.size == compressedSize) { "akt archive entry is truncated" }
                val raw = decompress(compressed, originalSize)
                require(raw.size == originalSize) { "akt archive entry size does not match" }
                entries[key] = raw
            }
            require(input.available() == 0) { "akt archive contains trailing data" }
        }
        return Collections.unmodifiableMap(entries)
    }

    private fun compress(raw: ByteArray, level: Int): ByteArray {
        val deflater = Deflater(level, true)
        return try {
            deflater.setInput(raw)
            deflater.finish()
            val output = ByteArrayOutputStream()
            val buffer = ByteArray(16 * 1024)
            while (!deflater.finished()) {
                val count = deflater.deflate(buffer)
                output.write(buffer, 0, count)
            }
            output.toByteArray()
        } finally {
            deflater.end()
        }
    }

    private fun decompress(compressed: ByteArray, originalSize: Int): ByteArray {
        val inflater = Inflater(true)
        return try {
            inflater.setInput(compressed)
            val output = ByteArray(originalSize)
            var offset = 0
            while (!inflater.finished() && offset < output.size) {
                val count = inflater.inflate(output, offset, output.size - offset)
                if (count == 0 && inflater.needsInput()) break
                offset += count
            }
            output.copyOf(offset)
        } finally {
            inflater.end()
        }
    }

    private fun encrypt(
        payload: ByteArray,
        password: CharArray,
        salt: ByteArray,
        nonce: ByteArray,
    ): ByteArray {
        val key = deriveKey(password, salt, defaultIterations)
        return try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(
                Cipher.ENCRYPT_MODE,
                SecretKeySpec(key, "AES"),
                GCMParameterSpec(tagLengthBits, nonce),
            )
            cipher.doFinal(payload)
        } finally {
            key.fill(0)
        }
    }

    private fun decrypt(
        payload: ByteArray,
        password: CharArray,
        salt: ByteArray,
        nonce: ByteArray,
        iterations: Int,
    ): ByteArray {
        val key = deriveKey(password, salt, iterations)
        return try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(
                Cipher.DECRYPT_MODE,
                SecretKeySpec(key, "AES"),
                GCMParameterSpec(tagLengthBits, nonce),
            )
            cipher.doFinal(payload)
        } catch (error: AEADBadTagException) {
            throw GeneralSecurityException("invalid archive password or corrupt archive", error)
        } finally {
            key.fill(0)
        }
    }

    private fun deriveKey(
        password: CharArray,
        salt: ByteArray,
        iterations: Int,
    ): ByteArray {
        val spec = PBEKeySpec(password, salt, iterations, keyLengthBits)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec)
                .encoded
        } finally {
            spec.clearPassword()
        }
    }
}

public class ArchiveAssetStore internal constructor(
    private val entries: Map<AssetKey, ByteArray>,
) : AssetStore {
    public val keys: Set<AssetKey>
        get() = entries.keys

    override fun read(key: AssetKey): ByteArray? = entries[key]?.copyOf()
}
