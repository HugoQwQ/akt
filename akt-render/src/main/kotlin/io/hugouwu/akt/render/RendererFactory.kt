package io.hugouwu.akt.render

import io.hugouwu.akt.assets.AssetStore

public fun interface RendererFactory {
    public fun create(
        window: WindowConfig,
        assets: AssetStore,
    ): RendererBackend
}
