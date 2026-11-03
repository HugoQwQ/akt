package io.hugouwu.akt.runtime

import io.hugouwu.akt.assets.AssetStore
import io.hugouwu.akt.core.StoryBuilder
import io.hugouwu.akt.core.StoryDefinition
import io.hugouwu.akt.core.visualNovel
import io.hugouwu.akt.render.RendererFactory
import io.hugouwu.akt.render.WindowConfig
import io.hugouwu.akt.render.VulkanRenderer

public fun aktGame(
    title: String,
    block: AktGameBuilder.() -> Unit,
): AktGame = AktGameBuilder(title).apply(block).build()

public class AktGameBuilder internal constructor(
    private val title: String,
) {
    private var windowConfig: WindowConfig = WindowConfig(title = title)
    private var assetStore: AssetStore = AssetStore.empty
    private var storyDefinition: StoryDefinition? = null
    private var rendererFactory: RendererFactory = RendererFactory(::VulkanRenderer)

    public fun window(block: WindowBuilder.() -> Unit) {
        windowConfig = WindowBuilder(windowConfig).apply(block).build()
    }

    public fun assets(store: AssetStore) {
        assetStore = store
    }

    public fun renderer(factory: RendererFactory) {
        rendererFactory = factory
    }

    public fun story(block: StoryBuilder.() -> Unit) {
        require(storyDefinition == null) { "a game can define only one story" }
        storyDefinition = visualNovel(title, block)
    }

    internal fun build(): AktGame = AktGame(
        story = requireNotNull(storyDefinition) { "a game story is required" },
        window = windowConfig,
        assets = assetStore,
        rendererFactory = rendererFactory,
    )
}

public class WindowBuilder internal constructor(
    private val current: WindowConfig,
) {
    private var title: String = current.title
    private var width: Int = current.width
    private var height: Int = current.height
    private var resizable: Boolean = current.resizable
    private var vsync: Boolean = current.vsync

    public fun title(value: String) {
        title = value
    }

    public fun size(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    public fun resizable(value: Boolean) {
        resizable = value
    }

    public fun vsync(value: Boolean) {
        vsync = value
    }

    internal fun build(): WindowConfig = WindowConfig(
        title = title,
        width = width,
        height = height,
        resizable = resizable,
        vsync = vsync,
    )
}

public class AktGame internal constructor(
    public val story: StoryDefinition,
    public val window: WindowConfig,
    public val assets: AssetStore,
    public val rendererFactory: RendererFactory,
) {
    public fun run() {
        AktLauncher.run(this)
    }
}
