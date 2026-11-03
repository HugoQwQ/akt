package io.hugouwu.akt.core

import kotlin.time.Duration

public fun revealText(
    text: String,
    elapsed: Duration,
    charactersPerSecond: Double,
): String {
    if (text.isEmpty() || charactersPerSecond <= 0.0) return text
    val characterCount = (elapsed.inWholeMilliseconds / 1_000.0 * charactersPerSecond)
        .toInt()
        .coerceAtLeast(0)
    return text.take(characterCount.coerceAtMost(text.length))
}
