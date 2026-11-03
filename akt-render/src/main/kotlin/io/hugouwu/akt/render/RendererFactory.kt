package io.hugouwu.akt.render

import io.hugouwu.akt.assets.AssetStore

/** creates renderer backends for the desktop runtime. */
public fun interface RendererFactory {
    /**
     * creates one renderer instance.
     *
     * @param window requested window settings.
     * @param assets assets used by [RendererBackend.render].
     * @throws IllegalStateException when the backend cannot start.
     */
    public fun create(
        window: WindowConfig,
        assets: AssetStore,
    ): RendererBackend
}
