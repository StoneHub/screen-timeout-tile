package com.stonecode.screentimeouttile

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeoutWidgetStateTest {

    @Test
    fun shouldNeedPermission_whenSettingsAreNotWritable() {
        assertEquals(
            TimeoutWidgetState.NeedsPermission,
            TimeoutWidgetState.from(canWriteSettings = false, currentTimeoutMs = 600_000),
        )
    }

    @Test
    fun shouldBeUnavailable_whenTimeoutCannotBeRead() {
        assertEquals(
            TimeoutWidgetState.Unavailable,
            TimeoutWidgetState.from(canWriteSettings = true, currentTimeoutMs = null),
        )
    }

    @Test
    fun shouldShowActiveMinutes_whenTimeoutIsLong() {
        assertEquals(
            TimeoutWidgetState.Timeout(TimeoutDurationLabel.Minutes(10), TimeoutTileVisualState.ACTIVE),
            TimeoutWidgetState.from(canWriteSettings = true, currentTimeoutMs = 600_000),
        )
    }

    @Test
    fun shouldShowInactiveSeconds_whenTimeoutIsShort() {
        assertEquals(
            TimeoutWidgetState.Timeout(TimeoutDurationLabel.Seconds(30), TimeoutTileVisualState.INACTIVE),
            TimeoutWidgetState.from(canWriteSettings = true, currentTimeoutMs = 30_000),
        )
    }

    @Test
    fun shouldTreatCustomSystemTimeoutAsActive_whenLongerThanShortPreset() {
        assertEquals(
            TimeoutWidgetState.Timeout(TimeoutDurationLabel.Minutes(2), TimeoutTileVisualState.ACTIVE),
            TimeoutWidgetState.from(canWriteSettings = true, currentTimeoutMs = 120_000),
        )
    }
}
