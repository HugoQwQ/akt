package io.hugouwu.akt.core

public data class Vec2(
    public val x: Float,
    public val y: Float,
) {
    public companion object {
        public val zero: Vec2 = Vec2(0f, 0f)
        public val one: Vec2 = Vec2(1f, 1f)
    }
}

public data class Rect(
    public val x: Float,
    public val y: Float,
    public val width: Float,
    public val height: Float,
) {
    public val center: Vec2
        get() = Vec2(x + width / 2f, y + height / 2f)

    public fun contains(point: Vec2): Boolean =
        point.x >= x && point.x <= x + width && point.y >= y && point.y <= y + height
}

public data class Transform(
    public val translation: Vec2 = Vec2.zero,
    public val scale: Vec2 = Vec2.one,
    public val rotationDegrees: Float = 0f,
    public val origin: Vec2 = Vec2(0.5f, 0.5f),
) {
    public companion object {
        public val identity: Transform = Transform()
    }
}

public data class Color(
    public val red: Float,
    public val green: Float,
    public val blue: Float,
    public val alpha: Float = 1f,
) {
    public fun withAlpha(value: Float): Color = copy(alpha = value.coerceIn(0f, 1f))

    public companion object {
        public val transparent: Color = Color(0f, 0f, 0f, 0f)
        public val black: Color = Color(0f, 0f, 0f)
        public val white: Color = Color(1f, 1f, 1f)

        public fun hex(value: String, alpha: Float = 1f): Color {
            val normalized = value.removePrefix("#")
            require(normalized.length == 6 || normalized.length == 8) {
                "color must use rrggbb or rrggbbaa"
            }
            val rgb = normalized.take(6).toLong(16)
            val alphaValue = normalized.takeIf { it.length == 8 }
                ?.takeLast(2)
                ?.toInt(16)
                ?.div(255f)
                ?: alpha
            return Color(
                red = ((rgb shr 16) and 0xff).toFloat() / 255f,
                green = ((rgb shr 8) and 0xff).toFloat() / 255f,
                blue = (rgb and 0xff).toFloat() / 255f,
                alpha = alphaValue.coerceIn(0f, 1f),
            )
        }
    }
}
