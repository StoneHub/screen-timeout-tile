package com.stonecode.screentimeouttile

class TimeoutTogglePolicy(
    val shortTimeoutMs: Int = SHORT_TIMEOUT_MS,
    val longTimeoutMs: Int = LONG_TIMEOUT_MS,
) {

    fun nextTimeout(currentTimeoutMs: Int): Int = if (currentTimeoutMs > shortTimeoutMs) {
        shortTimeoutMs
    } else {
        longTimeoutMs
    }

    fun visualStateFor(timeoutMs: Int): TimeoutTileVisualState = if (timeoutMs > shortTimeoutMs) {
        TimeoutTileVisualState.ACTIVE
    } else {
        TimeoutTileVisualState.INACTIVE
    }

    companion object {
        const val SHORT_TIMEOUT_MS = 30_000
        const val LONG_TIMEOUT_MS = 600_000
    }
}

enum class TimeoutTileVisualState {
    ACTIVE,
    INACTIVE,
}
