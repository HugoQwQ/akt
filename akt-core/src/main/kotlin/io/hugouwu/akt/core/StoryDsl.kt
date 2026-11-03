package io.hugouwu.akt.core

public fun visualNovel(
    title: String,
    block: StoryBuilder.() -> Unit,
): StoryDefinition = StoryBuilder(title).apply(block).build()

public class StoryBuilder internal constructor(
    private val title: String,
) {
    private val characters: MutableMap<String, CharacterDefinition> = linkedMapOf()
    private val scenes: MutableMap<String, SceneDefinition> = linkedMapOf()
    private var startScene: String? = null

    public fun character(
        id: String,
        displayName: String,
        nameColor: Color = Color.white,
    ) {
        require(id.isNotBlank()) { "character id cannot be blank" }
        require(!characters.containsKey(id)) { "character '$id' is already defined" }
        characters[id] = CharacterDefinition(id, displayName, nameColor)
    }

    public fun scene(
        id: String,
        block: SceneBuilder.() -> Unit,
    ) {
        require(id.isNotBlank()) { "scene id cannot be blank" }
        require(!scenes.containsKey(id)) { "scene '$id' is already defined" }
        scenes[id] = SceneBuilder(id).apply(block).build()
    }

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

public class SceneBuilder internal constructor(
    private val id: String,
) {
    private var background: AssetKey? = null
    private val cast: MutableList<ActorPlacement> = mutableListOf()
    private val lines: MutableList<DialogueLine> = mutableListOf()
    private val choices: MutableList<ChoiceDefinition> = mutableListOf()
    private val onEnter: MutableList<StoryAction> = mutableListOf()
    private var nextScene: String? = null
    private var exitTransition: Transition = Transition.Cut

    public fun background(key: String) {
        background = AssetKey(key)
    }

    public fun show(
        characterId: String,
        placement: ActorPlacement = ActorPlacement(characterId),
    ) {
        cast += placement.copy(characterId = characterId)
    }

    public fun say(
        speakerId: String,
        text: String,
        style: TextStyle = TextStyle(),
        actions: List<StoryAction> = emptyList(),
    ) {
        lines += DialogueLine(speakerId, text, style, actions)
    }

    public fun narrate(
        text: String,
        style: TextStyle = TextStyle(color = Color.hex("#d8d4cf")),
        actions: List<StoryAction> = emptyList(),
    ) {
        lines += DialogueLine(null, text, style, actions)
    }

    public fun choice(
        id: String,
        text: String,
        targetScene: String,
    ) {
        require(choices.none { it.id == id }) { "choice '$id' is already defined" }
        choices += ChoiceDefinition(id, text, targetScene)
    }

    public fun next(sceneId: String) {
        nextScene = sceneId
    }

    public fun transition(value: Transition) {
        exitTransition = value
    }

    public fun action(value: StoryAction) {
        onEnter += value
    }

    internal fun build(): SceneDefinition = SceneDefinition(
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

public fun position(x: Float, y: Float): Vec2 = Vec2(x, y)

public fun actor(
    position: Vec2,
    scale: Float = 1f,
    mirrored: Boolean = false,
): ActorPlacement = ActorPlacement(
    characterId = "",
    position = position,
    scale = scale,
    mirrored = mirrored,
)
