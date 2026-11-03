package io.hugouwu.akt.core

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * a stable key used to resolve a background, portrait, sound, or other source asset.
 *
 * @property value normalized asset path, normally using forward slashes.
 * @throws IllegalArgumentException when [value] is blank.
 */
@JvmInline
public value class AssetKey(public val value: String) {
    init {
        require(value.isNotBlank()) { "asset key cannot be blank" }
    }
}

/** horizontal text alignment inside a text item bounds. */
public enum class TextAlign {
    /** align text to the left edge. */
    LEFT,

    /** center text horizontally. */
    CENTER,

    /** align text to the right edge. */
    RIGHT,
}

/**
 * presentation settings for a dialogue line.
 *
 * @property size text size in ui pixels.
 * @property color text color.
 * @property align horizontal alignment.
 * @property lineHeight line advance multiplier.
 * @property revealSpeed reveal rate in characters per second. values at or below zero reveal the
 *   complete line immediately.
 */
public data class TextStyle(
    public val size: Float = 32f,
    public val color: Color = Color.white,
    public val align: TextAlign = TextAlign.LEFT,
    public val lineHeight: Float = 1.35f,
    public val revealSpeed: Double = 42.0,
)

/** a visual effect used while moving between scenes. */
public sealed interface Transition {
    /** total transition duration. */
    public val duration: Duration

    /** changes scenes immediately. */
    public data object Cut : Transition {
        override val duration: Duration = Duration.ZERO
    }

    /** fades through black for [duration]. */
    public data class Fade(override val duration: Duration) : Transition

    /** dissolves between scenes for [duration]. */
    public data class Dissolve(override val duration: Duration) : Transition

    /**
     * slides the next scene in from [direction] for [duration].
     *
     * @property direction edge from which the next scene appears.
     */
    public data class Slide(
        public val direction: SlideDirection,
        override val duration: Duration,
    ) : Transition

    /** shared transition defaults. */
    public companion object {
        /** a default 350 millisecond fade. */
        public val defaultFade: Transition = Fade(350.milliseconds)
    }
}

/** edge used by a sliding scene transition. */
public enum class SlideDirection {
    /** slide toward the left. */
    LEFT,

    /** slide toward the right. */
    RIGHT,

    /** slide toward the top. */
    UP,

    /** slide toward the bottom. */
    DOWN,
}

/** a declarative command attached to a scene or dialogue line. */
public sealed interface StoryAction {
    /**
     * sets a story variable.
     *
     * @property name variable name.
     * @property value new string value.
     */
    public data class SetVariable(
        public val name: String,
        public val value: String,
    ) : StoryAction

    /** requests playback of [asset]. */
    public data class PlaySound(public val asset: AssetKey) : StoryAction

    /** pauses story execution for [duration]. */
    public data class Wait(public val duration: Duration) : StoryAction

    /** emits a named event to a host integration. */
    public data class Emit(public val event: String) : StoryAction
}

/**
 * a named story character.
 *
 * @property id stable character identifier used by dialogue and scenes.
 * @property displayName name shown in the speaker label.
 * @property nameColor color used for the speaker label.
 */
public data class CharacterDefinition(
    public val id: String,
    public val displayName: String,
    public val nameColor: Color,
)

/**
 * placement of a character portrait in a scene.
 *
 * @property characterId character rendered by this placement.
 * @property position normalized viewport position for the portrait top center.
 * @property scale portrait scale multiplier.
 * @property mirrored whether the portrait is mirrored horizontally.
 */
public data class ActorPlacement(
    public val characterId: String,
    public val position: Vec2 = Vec2(0.7f, 0.2f),
    public val scale: Float = 1f,
    public val mirrored: Boolean = false,
)

/**
 * a selectable response that moves the session to another scene.
 *
 * @property id stable choice identifier.
 * @property text player-facing choice text.
 * @property targetScene scene entered after selection.
 */
public data class ChoiceDefinition(
    public val id: String,
    public val text: String,
    public val targetScene: String,
)

/**
 * one dialogue or narration line.
 *
 * @property speakerId character identifier, or null for narration.
 * @property text complete line text.
 * @property style line presentation settings.
 * @property actions commands associated with this line.
 */
public data class DialogueLine(
    public val speakerId: String?,
    public val text: String,
    public val style: TextStyle = TextStyle(),
    public val actions: List<StoryAction> = emptyList(),
)

/**
 * immutable scene data produced by [SceneBuilder].
 *
 * @property id stable scene identifier.
 * @property background optional background asset.
 * @property cast portraits shown in the scene.
 * @property lines ordered dialogue lines.
 * @property choices responses available after the final line.
 * @property nextScene optional automatic transition after dialogue.
 * @property exitTransition transition used when leaving this scene.
 * @property onEnter actions associated with entering this scene.
 */
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

/**
 * complete immutable story data.
 *
 * @property title game title.
 * @property startScene identifier of the initial scene.
 * @property characters character definitions indexed by id.
 * @property scenes scene definitions indexed by id.
 */
public data class StoryDefinition(
    public val title: String,
    public val startScene: String,
    public val characters: Map<String, CharacterDefinition>,
    public val scenes: Map<String, SceneDefinition>,
)

/**
 * a choice as exposed by an immutable [StoryFrame].
 *
 * @property id stable choice identifier.
 * @property text player-facing choice text.
 */
public data class FrameChoice(
    public val id: String,
    public val text: String,
)

/**
 * an immutable snapshot of the current story position and presentation.
 *
 * @property storyTitle title copied from the story definition.
 * @property sceneId active scene identifier.
 * @property background active background asset.
 * @property cast active portrait placements.
 * @property speaker active speaker, or null for narration.
 * @property text current line text.
 * @property textStyle current line text style.
 * @property choices choices available in the scene.
 * @property isAwaitingChoice whether a choice must be selected before advancing.
 * @property isComplete whether the story has ended.
 * @property transition scene transition currently being presented.
 */
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

/**
 * mutable story progression state over an immutable [story].
 *
 * a session is intended for one runtime thread. use [frame] to publish immutable snapshots to a
 * renderer or another frontend.
 *
 * @param story story definition to execute.
 * @throws IllegalArgumentException when [story] does not contain its start scene.
 */
public class StorySession(public val story: StoryDefinition) {
    private var sceneId: String = story.startScene
    private var lineIndex: Int = 0
    private var awaitingChoice: Boolean = false
    private var completed: Boolean = false
    private var transition: Transition? = null

    /** scene active at the current story position. */
    public val currentScene: SceneDefinition
        get() = story.scenes.getValue(sceneId)

    init {
        require(story.scenes.containsKey(story.startScene)) {
            "start scene '${story.startScene}' does not exist"
        }
        settleEmptyScene()
    }

    /** returns the current immutable story snapshot. */
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
            choices =
                if (scene.choices.isNotEmpty()) {
                    scene.choices.map { FrameChoice(it.id, it.text) }
                } else {
                    emptyList()
                },
            isAwaitingChoice = awaitingChoice,
            isComplete = completed,
            transition = transition,
        )
    }

    /**
     * advances by one line, enters the choice state, changes scenes, or completes the story.
     *
     * calling this while awaiting a choice or after completion returns the current frame unchanged.
     */
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

    /**
     * selects a choice while the session is awaiting one.
     *
     * @param choiceId identifier of the selected [FrameChoice].
     * @throws IllegalArgumentException when the story is not awaiting a choice or [choiceId] is not
     *   available.
     */
    public fun choose(choiceId: String): StoryFrame {
        require(awaitingChoice) { "the story is not waiting for a choice" }
        val scene = currentScene
        val choice =
            scene.choices.firstOrNull { it.id == choiceId }
                ?: throw IllegalArgumentException("choice '$choiceId' is not available")
        enterScene(choice.targetScene, scene.exitTransition)
        return frame()
    }

    /** resets progress to the start scene and returns the initial frame. */
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
