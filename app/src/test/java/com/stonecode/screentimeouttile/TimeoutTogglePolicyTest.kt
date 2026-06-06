package com.stonecode.screentimeouttile

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeoutTogglePolicyTest {

    private val policy = TimeoutTogglePolicy()

    @Test
    fun shouldChooseLongTimeout_whenCurrentTimeoutIsShortPreset() {
        assertEquals(600_000, policy.nextTimeout(30_000))
    }

    @Test
    fun shouldChooseShortTimeout_whenCurrentTimeoutIsLongPreset() {
        assertEquals(30_000, policy.nextTimeout(600_000))
    }

    @Test
    fun shouldChooseLongTimeout_whenCurrentTimeoutIsBelowOrEqualShortPreset() {
        assertEquals(600_000, policy.nextTimeout(15_000))
    }

    @Test
    fun shouldChooseShortTimeout_whenCurrentTimeoutIsBetweenPresets() {
        assertEquals(30_000, policy.nextTimeout(120_000))
    }

    @Test
    fun shouldChooseShortTimeout_whenCurrentTimeoutIsAboveLongPreset() {
        assertEquals(30_000, policy.nextTimeout(1_800_000))
    }

    @Test
    fun shouldMarkTimeoutActive_whenTimeoutIsLongerThanShortPreset() {
        assertEquals(TimeoutTileVisualState.ACTIVE, policy.visualStateFor(600_000))
    }

    @Test
    fun shouldMarkTimeoutInactive_whenTimeoutIsShortPreset() {
        assertEquals(TimeoutTileVisualState.INACTIVE, policy.visualStateFor(30_000))
    }
}
