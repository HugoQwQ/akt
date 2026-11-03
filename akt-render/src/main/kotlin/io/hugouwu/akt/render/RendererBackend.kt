package io.hugouwu.akt.render

import io.hugouwu.akt.ui.UiScene
import java.nio.file.Path

/** platform renderer lifecycle consumed by the game runtime. */
public interface RendererBackend : AutoCloseable {
    /** returns true when the window or host requested shutdown. */
    public fun shouldClose(): Boolean

    /** polls input for the current frame. */
    public fun pollInput(): InputFrame

    /** presents [scene] to the display. */
    public fun render(scene: UiScene)

    /** writes the next rendered frame to [path] as a png. */
    public fun requestCapture(path: Path)

    /** returns the host monotonic clock in seconds. */
    public fun elapsedSeconds(): Double
}
