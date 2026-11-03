package io.hugouwu.akt.render

import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.nio.ByteBuffer
import kotlin.math.ceil

internal class FontAtlas {
    internal data class Glyph(
        val u0: Float,
        val v0: Float,
        val u1: Float,
        val v1: Float,
        val advance: Float,
    )

    private val atlasFontSize: Float = 42f
    private val cellWidth: Int = 48
    private val cellHeight: Int = 64
    private val columns: Int = 16
    private val firstCharacter: Char = ' '
    private val lastCharacter: Char = '~'
    private val glyphs: Map<Char, Glyph>

    internal val width: Int
    internal val height: Int
    internal val pixels: ByteBuffer

    init {
        val count = lastCharacter.code - firstCharacter.code + 1
        val rows = ceil(count.toDouble() / columns).toInt()
        val image = BufferedImage(
            columns * cellWidth,
            rows * cellHeight,
            BufferedImage.TYPE_INT_ARGB,
        )
        width = image.width
        height = image.height
        val graphics: Graphics2D = image.createGraphics()
        graphics.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON,
        )
        graphics.font = Font("dialog", Font.PLAIN, atlasFontSize.toInt())
        graphics.color = Color.WHITE
        val metrics = graphics.fontMetrics
        val generated = linkedMapOf<Char, Glyph>()

        for (code in firstCharacter.code..lastCharacter.code) {
            val character = code.toChar()
            val index = code - firstCharacter.code
            val column = index % columns
            val row = index / columns
            val left = column * cellWidth
            val top = row * cellHeight
            graphics.drawString(character.toString(), left + 2, top + metrics.ascent + 2)
            generated[character] = Glyph(
                u0 = left.toFloat() / image.width,
                v0 = top.toFloat() / image.height,
                u1 = (left + cellWidth).toFloat() / image.width,
                v1 = (top + cellHeight).toFloat() / image.height,
                advance = metrics.charWidth(character).toFloat(),
            )
        }
        graphics.dispose()
        glyphs = generated

        pixels = uploadPixels(image)
    }

    internal fun glyph(character: Char): Glyph = glyphs[character] ?: glyphs.getValue('?')

    internal fun measure(text: String, size: Float): Float {
        val scale = size / atlasFontSize
        return text.sumOf { character ->
            glyph(character).advance.toDouble() * scale
        }.toFloat()
    }

    internal fun wrap(text: String, maxWidth: Float, size: Float): List<String> {
        val lines = mutableListOf<String>()
        text.split('\n').forEach { paragraph ->
            if (paragraph.isEmpty()) {
                lines += ""
                return@forEach
            }
            var current = ""
            paragraph.split(' ').forEach { word ->
                val candidate = if (current.isEmpty()) word else "$current $word"
                when {
                    measure(candidate, size) <= maxWidth -> current = candidate
                    current.isNotEmpty() -> {
                        lines += current
                        current = word
                    }
                    else -> {
                        var segment = ""
                        word.forEach { character ->
                            val next = segment + character
                            if (measure(next, size) > maxWidth && segment.isNotEmpty()) {
                                lines += segment
                                segment = character.toString()
                            } else {
                                segment = next
                            }
                        }
                        current = segment
                    }
                }
            }
            if (current.isNotEmpty()) lines += current
        }
        return lines
    }

    internal fun cellHeight(size: Float): Float = cellHeight * (size / atlasFontSize)

    internal fun cellWidth(size: Float): Float = cellWidth * (size / atlasFontSize)

    private fun uploadPixels(image: BufferedImage): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(image.width * image.height * 4)
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val argb = image.getRGB(x, y)
                buffer.put(((argb shr 16) and 0xff).toByte())
                buffer.put(((argb shr 8) and 0xff).toByte())
                buffer.put((argb and 0xff).toByte())
                buffer.put(((argb shr 24) and 0xff).toByte())
            }
        }
        buffer.flip()
        return buffer
    }
}
