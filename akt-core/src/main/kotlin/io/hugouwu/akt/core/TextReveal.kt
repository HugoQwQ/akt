package io.hugouwu.akt.core

import kotlin.time.Duration

/**
 * returns the prefix of [text] visible after [elapsed] time.
 *
 * @param text complete dialogue text.
 * @param elapsed time since the line reveal started.
 * @param charactersPerSecond reveal rate. values at or below zero reveal all text immediately.
 */
public fun revealText(
    text: String,
    elapsed: Duration,
    charactersPerSecond: Double,
): String {
    if (text.isEmpty() || charactersPerSecond <= 0.0) return text
    val characterCount =
        (elapsed.inWholeMilliseconds / 1_000.0 * charactersPerSecond).toInt().coerceAtLeast(0)
    return text.take(characterCount.coerceAtMost(text.length))
}
