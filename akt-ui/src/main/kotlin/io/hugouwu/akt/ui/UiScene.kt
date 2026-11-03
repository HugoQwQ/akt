package io.hugouwu.akt.ui

import io.hugouwu.akt.core.AssetKey
import io.hugouwu.akt.core.Color
import io.hugouwu.akt.core.Rect
import io.hugouwu.akt.core.TextStyle
import io.hugouwu.akt.core.Transform

public data class UiScene(
    public val width: Float,
    public val height: Float,
    public val clearColor: Color,
    public val items: List<UiItem>,
)

public sealed interface UiItem {
    public val id: String
    public val bounds: Rect
    public val layer: Int
    public val transform: Transform
}

public data class PanelItem(
    override val id: String,
    override val bounds: Rect,
    override val layer: Int = 0,
    override val transform: Transform = Transform.identity,
    public val color: Color,
) : UiItem

public enum class ImageFit {
    FILL,
}

public data class ImageItem(
    override val id: String,
    override val bounds: Rect,
    override val layer: Int = 0,
    override val transform: Transform = Transform.identity,
    public val asset: AssetKey,
    public val tint: Color = Color.white,
    public val fit: ImageFit = ImageFit.FILL,
) : UiItem

public data class TextItem(
    override val id: String,
    override val bounds: Rect,
    override val layer: Int = 0,
    override val transform: Transform = Transform.identity,
    public val text: String,
    public val style: TextStyle,
) : UiItem

public data class ChoiceItem(
    override val id: String,
    override val bounds: Rect,
    override val layer: Int = 0,
    override val transform: Transform = Transform.identity,
    public val index: Int,
    public val text: String,
    public val highlighted: Boolean,
    public val color: Color,
    public val textStyle: TextStyle,
) : UiItem
