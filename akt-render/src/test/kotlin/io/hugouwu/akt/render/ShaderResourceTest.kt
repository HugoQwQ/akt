package io.hugouwu.akt.render

import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ShaderResourceTest {
    @Test
    fun containsCompiledSpirvShaders() {
        listOf("ui.vert.spv", "ui.frag.spv").forEach { name ->
            val bytes =
                javaClass.getResourceAsStream("/shaders/$name")?.use { stream ->
                    stream.readBytes()
                }
            assertNotNull(bytes)
            val words = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).int
            assertEquals(0x07230203, words)
        }
    }
}
