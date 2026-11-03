package io.hugouwu.akt.render

import io.hugouwu.akt.core.Vec2

/**
 * desktop window and presentation settings.
 *
 * @property title window title.
 * @property width initial window width in screen pixels.
 * @property height initial window height in screen pixels.
 * @property resizable whether the operating system may resize the window.
 * @property vsync whether presentation should prefer vertical synchronization.
 * @throws IllegalArgumentException when the title is blank or either window dimension is not
 *   positive.
 */
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

/**
 * edge-triggered input collected for one frame.
 *
 * @property advance true on the frame enter or space is pressed.
 * @property choiceIndex zero-based number-key choice, or null when none was pressed.
 * @property pointer scene-space pointer position on click, or null when the pointer did not click.
 * @property closeRequested whether the user requested that the game stop.
 */
public data class InputFrame(
    public val advance: Boolean = false,
    public val choiceIndex: Int? = null,
    public val pointer: Vec2? = null,
    public val closeRequested: Boolean = false,
)
