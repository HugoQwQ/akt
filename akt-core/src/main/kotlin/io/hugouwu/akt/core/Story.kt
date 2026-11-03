package io.hugouwu.akt.core

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@JvmInline
public value class AssetKey(public val value: String) {
    init {
        require(value.isNotBlank()) { "asset key cannot be blank" }
    }
}

public enum class TextAlign {
    LEFT,
    CENTER,
    RIGHT,
}

public data class TextStyle(
    public val size: Float = 32f,
    public val color: Color = Color.white,
    public val align: TextAlign = TextAlign.LEFT,
    public val lineHeight: Float = 1.35f,
    public val revealSpeed: Double = 42.0,
)

public sealed interface Transition {
    public val duration: Duration

    public data object Cut : Transition {
        override val duration: Duration = Duration.ZERO
    }

    public data class Fade(override val duration: Duration) : Transition

    public data class Dissolve(override val duration: Duration) : Transition

    public data class Slide(
        public val direction: SlideDirection,
        override val duration: Duration,
    ) : Transition

    public companion object {
        public val defaultFade: Transition = Fade(350.milliseconds)
    }
}

public enum class SlideDirection {
    LEFT,
    RIGHT,
    UP,
    DOWN,
}

public sealed interface StoryAction {
    public data class SetVariable(
        public val name: String,
        public val value: String,
    ) : StoryAction

    public data class PlaySound(public val asset: AssetKey) : StoryAction

    public data class Wait(public val duration: Duration) : StoryAction

    public data class Emit(public val event: String) : StoryAction
}

public data class CharacterDefinition(
    public val id: String,
    public val displayName: String,
    public val nameColor: Color,
)

public data class ActorPlacement(
    public val characterId: String,
    public val position: Vec2 = Vec2(0.7f, 0.2f),
    public val scale: Float = 1f,
    public val mirrored: Boolean = false,
)

public data class ChoiceDefinition(
    public val id: String,
    public val text: String,
    public val targetScene: String,
)

public data class DialogueLine(
    public val speakerId: String?,
    public val text: String,
    public val style: TextStyle = TextStyle(),
    public val actions: List<StoryAction> = emptyList(),
)

public data class SceneDefinition(
    public val id: String,
    public val background: AssetKey?,
    public val cast: List<ActorPlacement>,
    public val lines: List<DialogueLine>,
    public val choices: List<ChoiceDefinition>,
    public val nextScene: String?,
    public val exitTransition: Transition,
    public val onEnter: List<StoryAction>,
)

public data class StoryDefinition(
    public val title: String,
    public val startScene: String,
    public val characters: Map<String, CharacterDefinition>,
    public val scenes: Map<String, SceneDefinition>,
)

public data class FrameChoice(
    public val id: String,
    public val text: String,
)

public data class StoryFrame(
    public val storyTitle: String,
    public val sceneId: String,
    public val background: AssetKey?,
    public val cast: List<ActorPlacement>,
    public val speaker: CharacterDefinition?,
    public val text: String,
    public val textStyle: TextStyle,
    public val choices: List<FrameChoice>,
    public val isAwaitingChoice: Boolean,
    public val isComplete: Boolean,
    public val transition: Transition?,
)

public class StorySession(
    public val story: StoryDefinition,
) {
    private var sceneId: String = story.startScene
    private var lineIndex: Int = 0
    private var awaitingChoice: Boolean = false
    private var completed: Boolean = false
    private var transition: Transition? = null

    public val currentScene: SceneDefinition
        get() = story.scenes.getValue(sceneId)

    init {
        require(story.scenes.containsKey(story.startScene)) {
            "start scene '${story.startScene}' does not exist"
        }
        settleEmptyScene()
    }

    public fun frame(): StoryFrame {
        val scene = currentScene
        val line = scene.lines.getOrNull(lineIndex)
        val speaker = line?.speakerId?.let(story.characters::get)
        return StoryFrame(
            storyTitle = story.title,
            sceneId = scene.id,
            background = scene.background,
            cast = scene.cast,
            speaker = speaker,
            text = line?.text.orEmpty(),
            textStyle = line?.style ?: TextStyle(),
            choices = if (scene.choices.isNotEmpty()) {
                scene.choices.map { FrameChoice(it.id, it.text) }
            } else {
                emptyList()
            },
            isAwaitingChoice = awaitingChoice,
            isComplete = completed,
            transition = transition,
        )
    }

    public fun advance(): StoryFrame {
        if (completed || awaitingChoice) return frame()

        val scene = currentScene
        if (lineIndex < scene.lines.lastIndex) {
            lineIndex += 1
            transition = null
            return frame()
        }

        if (scene.choices.isNotEmpty()) {
            awaitingChoice = true
            return frame()
        }

        if (scene.nextScene != null) {
            enterScene(scene.nextScene, scene.exitTransition)
        } else {
            completed = true
        }
        return frame()
    }

    public fun choose(choiceId: String): StoryFrame {
        require(awaitingChoice) { "the story is not waiting for a choice" }
        val scene = currentScene
        val choice = scene.choices.firstOrNull { it.id == choiceId }
            ?: throw IllegalArgumentException("choice '$choiceId' is not available")
        enterScene(choice.targetScene, scene.exitTransition)
        return frame()
    }

    public fun restart(): StoryFrame {
        sceneId = story.startScene
        lineIndex = 0
        awaitingChoice = false
        completed = false
        transition = null
        settleEmptyScene()
        return frame()
    }

    private fun enterScene(id: String, nextTransition: Transition) {
        require(story.scenes.containsKey(id)) { "scene '$id' does not exist" }
        sceneId = id
        lineIndex = 0
        awaitingChoice = false
        completed = false
        transition = nextTransition
        settleEmptyScene()
    }

    private fun settleEmptyScene() {
        val scene = currentScene
        if (scene.lines.isEmpty()) {
            when {
                scene.choices.isNotEmpty() -> awaitingChoice = true
                scene.nextScene != null -> enterScene(scene.nextScene, scene.exitTransition)
                else -> completed = true
            }
        }
    }
}
