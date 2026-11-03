package io.hugouwu.akt.render

import io.hugouwu.akt.ui.UiScene
import java.nio.file.Path

public interface RendererBackend : AutoCloseable {
    public fun shouldClose(): Boolean

    public fun pollInput(): InputFrame

    public fun render(scene: UiScene)

    public fun requestCapture(path: Path)

    public fun elapsedSeconds(): Double
}
