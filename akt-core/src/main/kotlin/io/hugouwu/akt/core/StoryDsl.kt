package io.hugouwu.akt.core

/**
 * builds an immutable story definition from the authoring dsl.
 *
 * @param title game title.
 * @param block story authoring block.
 * @throws IllegalArgumentException when the story has no start scene, duplicate ids, a missing
 *   start scene, or a reference to an unknown character or scene.
 */
public fun visualNovel(
    title: String,
    block: StoryBuilder.() -> Unit,
): StoryDefinition = StoryBuilder(title).apply(block).build()

/** mutable authoring scope passed to [visualNovel]. */
public class StoryBuilder internal constructor(private val title: String) {
    private val characters: MutableMap<String, CharacterDefinition> = linkedMapOf()
    private val scenes: MutableMap<String, SceneDefinition> = linkedMapOf()
    private var startScene: String? = null

    /**
     * defines a story character.
     *
     * @param id stable character identifier.
     * @param displayName speaker label text.
     * @param nameColor speaker label color.
     * @throws IllegalArgumentException when [id] is blank or already defined.
     */
    public fun character(
        id: String,
        displayName: String,
        nameColor: Color = Color.white,
    ) {
        require(id.isNotBlank()) { "character id cannot be blank" }
        require(!characters.containsKey(id)) { "character '$id' is already defined" }
        characters[id] = CharacterDefinition(id, displayName, nameColor)
    }

    /**
     * defines a scene.
     *
     * @param id stable scene identifier.
     * @param block scene authoring block.
     * @throws IllegalArgumentException when [id] is blank or already defined.
     */
    public fun scene(
        id: String,
        block: SceneBuilder.() -> Unit,
    ) {
        require(id.isNotBlank()) { "scene id cannot be blank" }
        require(!scenes.containsKey(id)) { "scene '$id' is already defined" }
        scenes[id] = SceneBuilder(id).apply(block).build()
    }

    /**
     * sets the initial scene.
     *
     * @param sceneId identifier of a scene defined in this builder.
     */
    public fun start(sceneId: String) {
        startScene = sceneId
    }

    internal fun build(): StoryDefinition {
        val initialScene = requireNotNull(startScene) { "a start scene is required" }
        require(scenes.containsKey(initialScene)) { "start scene '$initialScene' does not exist" }

        scenes.values.forEach { scene ->
            scene.cast.forEach { actor ->
                require(characters.containsKey(actor.characterId)) {
                    "scene '${scene.id}' uses unknown character '${actor.characterId}'"
                }
            }
            scene.lines.mapNotNull(DialogueLine::speakerId).forEach { speaker ->
                require(characters.containsKey(speaker)) {
                    "scene '${scene.id}' uses unknown speaker '$speaker'"
                }
            }
            scene.choices.forEach { choice ->
                require(scenes.containsKey(choice.targetScene)) {
                    "choice '${choice.id}' targets unknown scene '${choice.targetScene}'"
                }
            }
            scene.nextScene?.let { next ->
                require(scenes.containsKey(next)) {
                    "scene '${scene.id}' targets unknown scene '$next'"
                }
            }
        }

        return StoryDefinition(
            title = title,
            startScene = initialScene,
            characters = characters.toMap(),
            scenes = scenes.toMap(),
        )
    }
}

/** mutable authoring scope for one [SceneBuilder] instance. */
public class SceneBuilder internal constructor(private val id: String) {
    private var background: AssetKey? = null
    private val cast: MutableList<ActorPlacement> = mutableListOf()
    private val lines: MutableList<DialogueLine> = mutableListOf()
    private val choices: MutableList<ChoiceDefinition> = mutableListOf()
    private val onEnter: MutableList<StoryAction> = mutableListOf()
    private var nextScene: String? = null
    private var exitTransition: Transition = Transition.Cut

    /**
     * sets the scene background.
     *
     * @param key asset key, normally `backgrounds/<name>`.
     * @throws IllegalArgumentException when [key] is blank.
     */
    public fun background(key: String) {
        background = AssetKey(key)
    }

    /**
     * shows a character portrait.
     *
     * @param characterId character to show.
     * @param placement normalized portrait placement. its character id is replaced with
     *   [characterId].
     */
    public fun show(
        characterId: String,
        placement: ActorPlacement = ActorPlacement(characterId),
    ) {
        cast += placement.copy(characterId = characterId)
    }

    /**
     * appends a spoken dialogue line.
     *
     * @param speakerId character speaking the line.
     * @param text complete line text.
     * @param style line presentation settings.
     * @param actions commands associated with the line.
     */
    public fun say(
        speakerId: String,
        text: String,
        style: TextStyle = TextStyle(),
        actions: List<StoryAction> = emptyList(),
    ) {
        lines += DialogueLine(speakerId, text, style, actions)
    }

    /**
     * appends a narration line without a speaker.
     *
     * @param text complete line text.
     * @param style line presentation settings.
     * @param actions commands associated with the line.
     */
    public fun narrate(
        text: String,
        style: TextStyle = TextStyle(color = Color.hex("#d8d4cf")),
        actions: List<StoryAction> = emptyList(),
    ) {
        lines += DialogueLine(null, text, style, actions)
    }

    /**
     * appends a response shown after the final dialogue line.
     *
     * @param id stable choice identifier.
     * @param text player-facing choice text.
     * @param targetScene scene entered after selection.
     * @throws IllegalArgumentException when [id] is already defined in this scene.
     */
    public fun choice(
        id: String,
        text: String,
        targetScene: String,
    ) {
        require(choices.none { it.id == id }) { "choice '$id' is already defined" }
        choices += ChoiceDefinition(id, text, targetScene)
    }

    /**
     * sets an automatic next scene after the final line when no choices exist.
     *
     * @param sceneId target scene identifier.
     */
    public fun next(sceneId: String) {
        nextScene = sceneId
    }

    /**
     * sets the transition used when this scene is left.
     *
     * @param value transition definition.
     */
    public fun transition(value: Transition) {
        exitTransition = value
    }

    /**
     * appends an action associated with entering this scene.
     *
     * @param value declarative action.
     */
    public fun action(value: StoryAction) {
        onEnter += value
    }

    internal fun build(): SceneDefinition =
        SceneDefinition(
            id = id,
            background = background,
            cast = cast.toList(),
            lines = lines.toList(),
            choices = choices.toList(),
            nextScene = nextScene,
            exitTransition = exitTransition,
            onEnter = onEnter.toList(),
        )
}

/**
 * creates a normalized actor position.
 *
 * @param x horizontal fraction in the zero-to-one viewport range.
 * @param y vertical fraction in the zero-to-one viewport range.
 */
public fun position(x: Float, y: Float): Vec2 = Vec2(x, y)

/**
 * creates a reusable portrait placement.
 *
 * [SceneBuilder.show] replaces the blank character id with the character being shown.
 *
 * @param position normalized portrait top-center position.
 * @param scale portrait scale multiplier.
 * @param mirrored whether the portrait is mirrored horizontally.
 */
public fun actor(
    position: Vec2,
    scale: Float = 1f,
    mirrored: Boolean = false,
): ActorPlacement =
    ActorPlacement(
        characterId = "",
        position = position,
        scale = scale,
        mirrored = mirrored,
    )
