package io.hugouwu.akt.runtime

import io.hugouwu.akt.core.StoryDefinition
import io.hugouwu.akt.core.StoryFrame
import io.hugouwu.akt.core.StorySession
import io.hugouwu.akt.core.Transition
import io.hugouwu.akt.render.InputFrame
import io.hugouwu.akt.ui.ChoiceItem
import io.hugouwu.akt.ui.UiScene
import kotlin.math.min

internal class StoryController(story: StoryDefinition) {
    private val session: StorySession = StorySession(story)
    private var frame: StoryFrame = session.frame()
    private var revealedCharacters: Float = 0f
    private var transitionElapsed: Float = 0f
    private var transitionDuration: Float = 0f

    internal val currentFrame: StoryFrame
        get() = frame

    internal val visibleText: String
        get() = frame.text.take(revealedCharacters.toInt().coerceAtMost(frame.text.length))

    internal val transitionAlpha: Float
        get() =
            when {
                transitionDuration <= 0f -> 1f
                else -> (transitionElapsed / transitionDuration).coerceIn(0f, 1f)
            }

    internal fun update(
        deltaSeconds: Float,
        input: InputFrame,
        previousScene: UiScene?,
    ) {
        val clickedChoice =
            input.pointer?.let { pointer ->
                previousScene
                    ?.items
                    ?.filterIsInstance<ChoiceItem>()
                    ?.firstOrNull { it.bounds.contains(pointer) }
                    ?.index
            }
        val requestedChoice = input.choiceIndex ?: clickedChoice

        when {
            frame.isAwaitingChoice && requestedChoice != null -> {
                choose(requestedChoice)
                return
            }
            requestedChoice != null -> return
            input.advance -> {
                advance()
                return
            }
            input.pointer != null && frame.isAwaitingChoice -> return
            input.pointer != null -> {
                advance()
                return
            }
        }

        if (!frame.isAwaitingChoice) {
            val speed = frame.textStyle.revealSpeed.toFloat()
            revealedCharacters =
                min(
                    frame.text.length.toFloat(),
                    revealedCharacters + speed * deltaSeconds.coerceAtLeast(0f),
                )
        }
        transitionElapsed =
            min(transitionDuration, transitionElapsed + deltaSeconds.coerceAtLeast(0f))
    }

    private fun advance() {
        if (frame.isComplete || frame.isAwaitingChoice) return
        if (revealedCharacters < frame.text.length) {
            revealedCharacters = frame.text.length.toFloat()
            return
        }

        val previousSceneId = frame.sceneId
        frame = session.advance()
        if (frame.isComplete) {
            revealedCharacters = frame.text.length.toFloat()
            transitionDuration = 0f
            transitionElapsed = 0f
        } else if (frame.sceneId != previousSceneId) {
            beginTransition(frame.transition)
        } else {
            beginLine()
        }
    }

    private fun choose(index: Int) {
        if (revealedCharacters < frame.text.length) {
            revealedCharacters = frame.text.length.toFloat()
            return
        }
        val choice = frame.choices.getOrNull(index) ?: return
        frame = session.choose(choice.id)
        beginTransition(frame.transition)
    }

    private fun beginLine() {
        revealedCharacters = 0f
        transitionElapsed = 0f
        transitionDuration = 0f
    }

    private fun beginTransition(transition: Transition?) {
        beginLine()
        transitionDuration = transition?.duration?.inWholeMilliseconds?.div(1_000f) ?: 0f
    }
}
