package io.hugouwu.akt.ui

import io.hugouwu.akt.core.AssetKey
import io.hugouwu.akt.core.Color
import io.hugouwu.akt.core.Rect
import io.hugouwu.akt.core.StoryFrame
import io.hugouwu.akt.core.TextAlign
import io.hugouwu.akt.core.TextStyle
import io.hugouwu.akt.core.Transform
import io.hugouwu.akt.core.Vec2
import kotlin.math.roundToInt

public data class UiMetrics(
    public val safeMargin: Float = 72f,
    public val dialogueHeight: Float = 188f,
    public val choiceHeight: Float = 54f,
    public val choiceGap: Float = 12f,
)

public class VisualNovelLayout(
    private val metrics: UiMetrics = UiMetrics(),
) {
    public fun layout(
        frame: StoryFrame,
        width: Float,
        height: Float,
        visibleText: String,
        highlightedChoice: Int? = null,
        transitionAlpha: Float = 1f,
    ): UiScene {
        val items = mutableListOf<UiItem>()
        val background = frame.background ?: AssetKey("background/fallback")
        items += ImageItem(
            id = "background",
            bounds = Rect(0f, 0f, width, height),
            layer = 0,
            asset = background,
        )
        items += PanelItem(
            id = "background-shade",
            bounds = Rect(0f, 0f, width, height),
            layer = 1,
            color = Color(0.02f, 0.025f, 0.04f, 0.28f),
        )

        frame.cast.forEachIndexed { index, actor ->
            val portraitHeight = height * 0.78f * actor.scale
            val portraitWidth = portraitHeight * 0.64f
            val centerX = width * actor.position.x
            val top = height * actor.position.y
            items += ImageItem(
                id = "actor-${actor.characterId}-$index",
                bounds = Rect(
                    x = centerX - portraitWidth / 2f,
                    y = top,
                    width = portraitWidth,
                    height = portraitHeight,
                ),
                layer = 10 + index,
                transform = Transform(
                    scale = Vec2(if (actor.mirrored) -1f else 1f, 1f),
                ),
                asset = AssetKey("characters/${actor.characterId}"),
                tint = Color.white.withAlpha(0.96f),
            )
        }

        if (frame.isAwaitingChoice) {
            layoutChoices(frame, width, height, highlightedChoice, items)
        }

        val dialogueTop = height - metrics.safeMargin - metrics.dialogueHeight
        items += PanelItem(
            id = "dialogue-panel",
            bounds = Rect(
                metrics.safeMargin,
                dialogueTop,
                width - metrics.safeMargin * 2f,
                metrics.dialogueHeight,
            ),
            layer = 100,
            color = Color(0.035f, 0.045f, 0.07f, 0.92f),
        )
        items += PanelItem(
            id = "dialogue-accent",
            bounds = Rect(metrics.safeMargin, dialogueTop, 5f, metrics.dialogueHeight),
            layer = 101,
            color = Color.hex("#d2a86a"),
        )

        val speakerLabel = frame.speaker?.displayName
        val textBounds = if (speakerLabel != null) {
            items += TextItem(
                id = "speaker",
                bounds = Rect(
                    metrics.safeMargin + 28f,
                    dialogueTop + 18f,
                    520f,
                    42f,
                ),
                layer = 102,
                text = speakerLabel,
                style = TextStyle(
                    size = 28f,
                    color = frame.speaker?.nameColor ?: Color.white,
                ),
            )
            Rect(
                metrics.safeMargin + 28f,
                dialogueTop + 62f,
                width - metrics.safeMargin * 2f - 56f,
                metrics.dialogueHeight - 82f,
            )
        } else {
            Rect(
                metrics.safeMargin + 28f,
                dialogueTop + 30f,
                width - metrics.safeMargin * 2f - 56f,
                metrics.dialogueHeight - 54f,
            )
        }

        items += TextItem(
            id = "dialogue-text",
            bounds = textBounds,
            layer = 103,
            text = visibleText,
            style = frame.textStyle,
        )

        if (!frame.isComplete) {
            items += TextItem(
                id = "continue-hint",
                bounds = Rect(width - metrics.safeMargin - 260f, height - 40f, 230f, 24f),
                layer = 104,
                text = if (frame.isAwaitingChoice) {
                    "select a response"
                } else {
                    "enter - continue"
                },
                style = TextStyle(
                    size = 18f,
                    color = Color(0.75f, 0.72f, 0.68f, 0.72f),
                    align = TextAlign.RIGHT,
                ),
            )
        }

        if (transitionAlpha < 1f) {
            items += PanelItem(
                id = "transition",
                bounds = Rect(0f, 0f, width, height),
                layer = 1_000,
                color = Color.black.withAlpha((1f - transitionAlpha).coerceIn(0f, 1f)),
            )
        }

        return UiScene(
            width = width,
            height = height,
            clearColor = Color.hex("#080b12"),
            items = items.sortedBy(UiItem::layer),
        )
    }

    private fun layoutChoices(
        frame: StoryFrame,
        width: Float,
        height: Float,
        highlightedChoice: Int?,
        items: MutableList<UiItem>,
    ) {
        if (frame.choices.isEmpty()) return
        val choiceWidth = (width * 0.42f).coerceAtMost(620f)
        val totalHeight = frame.choices.size * metrics.choiceHeight +
            (frame.choices.size - 1) * metrics.choiceGap
        val startY = height - metrics.safeMargin - metrics.dialogueHeight -
            totalHeight - 28f
        val startX = metrics.safeMargin + 18f

        frame.choices.forEachIndexed { index, choice ->
            val y = startY + index * (metrics.choiceHeight + metrics.choiceGap)
            val highlighted = highlightedChoice == index
            items += ChoiceItem(
                id = "choice-${choice.id}",
                bounds = Rect(startX, y, choiceWidth, metrics.choiceHeight),
                layer = 90 + index,
                index = index,
                text = "${index + 1}. ${choice.text}",
                highlighted = highlighted,
                color = if (highlighted) {
                    Color(0.39f, 0.28f, 0.15f, 0.96f)
                } else {
                    Color(0.035f, 0.045f, 0.07f, 0.9f)
                },
                textStyle = TextStyle(
                    size = 24f,
                    color = Color.hex("#f1e8dc"),
                    align = TextAlign.LEFT,
                ),
            )
        }
    }
}

public fun scaledPixel(value: Float): Int = (value.coerceAtLeast(0f)).roundToInt()
