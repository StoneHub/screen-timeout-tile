package com.stonecode.screentimeouttile

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SetupUiStateTest {

    @Test
    fun shouldAskForPermission_whenSettingsWriteAccessIsMissing() {
        val state = SetupUiState.from(
            canWriteSettings = false,
            currentTimeoutMs = null,
            canRequestTilePlacement = true,
        )

        assertEquals(SetupAction.GRANT_PERMISSION, state.primaryAction)
        assertEquals(TimeoutStatus.Unavailable, state.timeoutStatus)
        assertFalse(state.canRequestTilePlacement)
    }

    @Test
    fun shouldOfferTilePlacement_whenPermissionExistsAndPlatformSupportsIt() {
        val state = SetupUiState.from(
            canWriteSettings = true,
            currentTimeoutMs = 600_000,
            canRequestTilePlacement = true,
        )

        assertEquals(SetupAction.ADD_TILE, state.primaryAction)
        assertEquals(TimeoutStatus.Available(TimeoutDurationLabel.Minutes(10)), state.timeoutStatus)
        assertTrue(state.canRequestTilePlacement)
    }

    @Test
    fun shouldShowManualInstructions_whenTilePlacementRequestIsUnsupported() {
        val state = SetupUiState.from(
            canWriteSettings = true,
            currentTimeoutMs = 30_000,
            canRequestTilePlacement = false,
        )

        assertEquals(SetupAction.MANUAL_ADD_TILE, state.primaryAction)
        assertEquals(TimeoutStatus.Available(TimeoutDurationLabel.Seconds(30)), state.timeoutStatus)
        assertFalse(state.canRequestTilePlacement)
    }
}
