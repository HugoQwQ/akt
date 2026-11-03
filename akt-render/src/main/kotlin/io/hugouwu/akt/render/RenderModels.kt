package io.hugouwu.akt.render

import io.hugouwu.akt.core.Vec2

public data class WindowConfig(
    public val title: String = "akt",
    public val width: Int = 1280,
    public val height: Int = 720,
    public val resizable: Boolean = true,
    public val vsync: Boolean = true,
) {
    init {
        require(title.isNotBlank()) { "window title cannot be blank" }
        require(width > 0 && height > 0) { "window size must be positive" }
    }
}

public data class InputFrame(
    public val advance: Boolean = false,
    public val choiceIndex: Int? = null,
    public val pointer: Vec2? = null,
    public val closeRequested: Boolean = false,
)
