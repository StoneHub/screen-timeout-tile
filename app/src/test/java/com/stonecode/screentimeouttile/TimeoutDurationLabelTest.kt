package com.stonecode.screentimeouttile

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeoutDurationLabelTest {

    @Test
    fun shouldFormatSeconds_whenTimeoutIsUnderOneMinute() {
        assertEquals(TimeoutDurationLabel.Seconds(30), TimeoutDurationLabel.fromMillis(30_000))
    }

    @Test
    fun shouldFormatWholeMinutes_whenTimeoutIsAtLeastOneMinute() {
        assertEquals(TimeoutDurationLabel.Minutes(10), TimeoutDurationLabel.fromMillis(600_000))
    }
}
