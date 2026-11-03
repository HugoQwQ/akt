package io.hugouwu.akt.core

/**
 * a two-dimensional coordinate in the coordinate space selected by its caller.
 *
 * @property x horizontal component.
 * @property y vertical component.
 */
public data class Vec2(
    public val x: Float,
    public val y: Float,
) {
    /** shared constants used by geometry defaults. */
    public companion object {
        /** the origin. */
        public val zero: Vec2 = Vec2(0f, 0f)

        /** a coordinate with both components set to one. */
        public val one: Vec2 = Vec2(1f, 1f)
    }
}

/**
 * an axis-aligned rectangle in ui coordinate space.
 *
 * @property x left edge.
 * @property y top edge.
 * @property width horizontal size.
 * @property height vertical size.
 */
public data class Rect(
    public val x: Float,
    public val y: Float,
    public val width: Float,
    public val height: Float,
) {
    /** geometric center of this rectangle. */
    public val center: Vec2
        get() = Vec2(x + width / 2f, y + height / 2f)

    /** returns true when [point] is inside or on the rectangle edge. */
    public fun contains(point: Vec2): Boolean =
        point.x >= x && point.x <= x + width && point.y >= y && point.y <= y + height
}

/**
 * a painter-order transform applied around an item bounds origin.
 *
 * @property translation offset in ui pixels.
 * @property scale horizontal and vertical scale factors.
 * @property rotationDegrees clockwise rotation in degrees.
 * @property origin normalized transform origin within the item bounds.
 */
public data class Transform(
    public val translation: Vec2 = Vec2.zero,
    public val scale: Vec2 = Vec2.one,
    public val rotationDegrees: Float = 0f,
    public val origin: Vec2 = Vec2(0.5f, 0.5f),
) {
    /** shared transform constants. */
    public companion object {
        /** a transform that leaves geometry unchanged. */
        public val identity: Transform = Transform()
    }
}

/**
 * a non-premultiplied rgba color with components in the zero-to-one range.
 *
 * @property red red component.
 * @property green green component.
 * @property blue blue component.
 * @property alpha opacity component.
 */
public data class Color(
    public val red: Float,
    public val green: Float,
    public val blue: Float,
    public val alpha: Float = 1f,
) {
    /** returns a copy with [alpha] clamped to the valid range. */
    public fun withAlpha(value: Float): Color = copy(alpha = value.coerceIn(0f, 1f))

    /** common colors and hexadecimal parsing helpers. */
    public companion object {
        /** a fully transparent color. */
        public val transparent: Color = Color(0f, 0f, 0f, 0f)

        /** opaque black. */
        public val black: Color = Color(0f, 0f, 0f)

        /** opaque white. */
        public val white: Color = Color(1f, 1f, 1f)

        /**
         * parses `rrggbb` or `rrggbbaa` hex, with an optional leading hash.
         *
         * @param value hexadecimal color text.
         * @param alpha fallback alpha when [value] has no alpha component.
         * @throws IllegalArgumentException when [value] has an unsupported format.
         */
        public fun hex(value: String, alpha: Float = 1f): Color {
            val normalized = value.removePrefix("#")
            require(normalized.length == 6 || normalized.length == 8) {
                "color must use rrggbb or rrggbbaa"
            }
            val rgb = normalized.take(6).toLong(16)
            val alphaValue =
                normalized.takeIf { it.length == 8 }?.takeLast(2)?.toInt(16)?.div(255f) ?: alpha
            return Color(
                red = ((rgb shr 16) and 0xff).toFloat() / 255f,
                green = ((rgb shr 8) and 0xff).toFloat() / 255f,
                blue = (rgb and 0xff).toFloat() / 255f,
                alpha = alphaValue.coerceIn(0f, 1f),
            )
        }
    }
}
