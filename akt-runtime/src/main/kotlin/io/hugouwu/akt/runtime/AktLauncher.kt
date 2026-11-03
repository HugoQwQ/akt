package io.hugouwu.akt.runtime

import io.hugouwu.akt.ui.UiScene
import io.hugouwu.akt.ui.VisualNovelLayout
import org.slf4j.LoggerFactory

/** desktop game loop used by [AktGame.run]. */
public object AktLauncher {
    private val logger = LoggerFactory.getLogger(AktLauncher::class.java)

    /**
     * runs [game] until the renderer or user requests shutdown.
     *
     * @throws IllegalStateException when the renderer cannot start.
     */
    public fun run(game: AktGame) {
        logger.info("starting game '{}'", game.story.title)
        val layout = VisualNovelLayout()
        game.rendererFactory.create(game.window, game.assets).use { renderer ->
            val controller = StoryController(game.story)
            var previousScene: UiScene? = null
            var previousTime = renderer.elapsedSeconds()

            while (!renderer.shouldClose()) {
                val now = renderer.elapsedSeconds()
                val deltaSeconds = (now - previousTime).toFloat().coerceIn(0f, 0.1f)
                previousTime = now

                val input = renderer.pollInput()
                if (input.closeRequested) break
                controller.update(deltaSeconds, input, previousScene)

                val scene =
                    layout.layout(
                        frame = controller.currentFrame,
                        width = game.window.width.toFloat(),
                        height = game.window.height.toFloat(),
                        visibleText = controller.visibleText,
                        transitionAlpha = controller.transitionAlpha,
                    )
                renderer.render(scene)
                previousScene = scene
            }
        }
        logger.info("game stopped")
    }
}
