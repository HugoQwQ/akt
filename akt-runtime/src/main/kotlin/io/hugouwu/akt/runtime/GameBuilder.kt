package io.hugouwu.akt.runtime

import io.hugouwu.akt.assets.AssetStore
import io.hugouwu.akt.core.StoryBuilder
import io.hugouwu.akt.core.StoryDefinition
import io.hugouwu.akt.core.visualNovel
import io.hugouwu.akt.render.RendererFactory
import io.hugouwu.akt.render.VulkanRenderer
import io.hugouwu.akt.render.WindowConfig

/**
 * builds a runnable game from the runtime dsl.
 *
 * @param title default game and window title.
 * @param block game configuration and story authoring block.
 * @throws IllegalArgumentException when the story is missing or contains invalid references.
 */
public fun aktGame(
    title: String,
    block: AktGameBuilder.() -> Unit,
): AktGame = AktGameBuilder(title).apply(block).build()

/** mutable authoring scope for [aktGame]. */
public class AktGameBuilder internal constructor(private val title: String) {
    private var windowConfig: WindowConfig = WindowConfig(title = title)
    private var assetStore: AssetStore = AssetStore.empty
    private var storyDefinition: StoryDefinition? = null
    private var rendererFactory: RendererFactory = RendererFactory(::VulkanRenderer)

    /**
     * changes window settings.
     *
     * @param block window configuration block.
     */
    public fun window(block: WindowBuilder.() -> Unit) {
        windowConfig = WindowBuilder(windowConfig).apply(block).build()
    }

    /**
     * sets the runtime asset source.
     *
     * @param store directory, archive, or custom asset store.
     */
    public fun assets(store: AssetStore) {
        assetStore = store
    }

    /**
     * replaces the default vulkan renderer factory.
     *
     * @param factory renderer factory used when [AktGame.run] starts.
     */
    public fun renderer(factory: RendererFactory) {
        rendererFactory = factory
    }

    /**
     * defines the game story.
     *
     * @param block story authoring block.
     * @throws IllegalArgumentException when a story was already defined or the story definition is
     *   invalid.
     */
    public fun story(block: StoryBuilder.() -> Unit) {
        require(storyDefinition == null) { "a game can define only one story" }
        storyDefinition = visualNovel(title, block)
    }

    internal fun build(): AktGame =
        AktGame(
            story = requireNotNull(storyDefinition) { "a game story is required" },
            window = windowConfig,
            assets = assetStore,
            rendererFactory = rendererFactory,
        )
}

/** mutable window configuration scope for [AktGameBuilder.window]. */
public class WindowBuilder internal constructor(private val current: WindowConfig) {
    private var title: String = current.title
    private var width: Int = current.width
    private var height: Int = current.height
    private var resizable: Boolean = current.resizable
    private var vsync: Boolean = current.vsync

    /** changes the window title. */
    public fun title(value: String) {
        title = value
    }

    /** changes the initial window size in screen pixels. */
    public fun size(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    /** controls whether the window can be resized. */
    public fun resizable(value: Boolean) {
        resizable = value
    }

    /** controls whether presentation prefers vertical synchronization. */
    public fun vsync(value: Boolean) {
        vsync = value
    }

    internal fun build(): WindowConfig =
        WindowConfig(
            title = title,
            width = width,
            height = height,
            resizable = resizable,
            vsync = vsync,
        )
}

/**
 * an immutable runnable game definition.
 *
 * @property story story data executed by [run].
 * @property window desktop window settings.
 * @property assets runtime asset source.
 * @property rendererFactory factory used to create the active renderer.
 */
public class AktGame
internal constructor(
    public val story: StoryDefinition,
    public val window: WindowConfig,
    public val assets: AssetStore,
    public val rendererFactory: RendererFactory,
) {
    /**
     * starts the desktop runtime and blocks until the game closes.
     *
     * @throws IllegalStateException when the configured renderer cannot start.
     */
    public fun run() {
        AktLauncher.run(this)
    }
}
