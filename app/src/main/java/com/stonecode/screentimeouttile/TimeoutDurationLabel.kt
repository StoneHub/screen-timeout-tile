package com.stonecode.screentimeouttile

sealed interface TimeoutDurationLabel {
    val value: Int

    data class Seconds(override val value: Int) : TimeoutDurationLabel
    data class Minutes(override val value: Int) : TimeoutDurationLabel

    companion object {
        fun fromMillis(timeoutMs: Int): TimeoutDurationLabel {
            val seconds = timeoutMs / 1_000
            return if (seconds >= 60) {
                Minutes(seconds / 60)
            } else {
                Seconds(seconds)
            }
        }
    }
}
