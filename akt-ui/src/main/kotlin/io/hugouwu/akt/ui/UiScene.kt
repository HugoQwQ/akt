package io.hugouwu.akt.ui

import io.hugouwu.akt.core.AssetKey
import io.hugouwu.akt.core.Color
import io.hugouwu.akt.core.Rect
import io.hugouwu.akt.core.TextStyle
import io.hugouwu.akt.core.Transform

/**
 * renderer-independent description of one frame.
 *
 * @property width logical viewport width.
 * @property height logical viewport height.
 * @property clearColor color used outside all items.
 * @property items draw items, sorted by [UiItem.layer] before rendering.
 */
public data class UiScene(
    public val width: Float,
    public val height: Float,
    public val clearColor: Color,
    public val items: List<UiItem>,
)

/** one drawable item in a [UiScene]. */
public sealed interface UiItem {
    /** stable identifier used by input and visual tests. */
    public val id: String

    /** untransformed item bounds in ui coordinates. */
    public val bounds: Rect

    /** painter-order layer. larger values draw later. */
    public val layer: Int

    /** transform applied around [bounds]. */
    public val transform: Transform
}

/**
 * a solid-color rectangle.
 *
 * @property color fill color.
 */
public data class PanelItem(
    override val id: String,
    override val bounds: Rect,
    override val layer: Int = 0,
    override val transform: Transform = Transform.identity,
    public val color: Color,
) : UiItem

/** how an image is fitted into its bounds. */
public enum class ImageFit {
    /** stretch the image to fill the complete bounds. */
    FILL
}

/**
 * an image resolved through the runtime asset store.
 *
 * @property asset image asset key.
 * @property tint multiplier applied to sampled image color.
 * @property fit image fitting mode.
 */
public data class ImageItem(
    override val id: String,
    override val bounds: Rect,
    override val layer: Int = 0,
    override val transform: Transform = Transform.identity,
    public val asset: AssetKey,
    public val tint: Color = Color.white,
    public val fit: ImageFit = ImageFit.FILL,
) : UiItem

/**
 * a wrapped text run.
 *
 * @property text visible text.
 * @property style text presentation settings.
 */
public data class TextItem(
    override val id: String,
    override val bounds: Rect,
    override val layer: Int = 0,
    override val transform: Transform = Transform.identity,
    public val text: String,
    public val style: TextStyle,
) : UiItem

/**
 * an interactive dialogue response.
 *
 * @property index zero-based choice index.
 * @property text player-facing label.
 * @property highlighted whether this item is currently highlighted.
 * @property color panel color.
 * @property textStyle label text style.
 */
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
