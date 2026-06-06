package com.stonecode.screentimeouttile

data class SetupUiState(
    val primaryAction: SetupAction,
    val timeoutStatus: TimeoutStatus,
    val canRequestTilePlacement: Boolean,
) {
    companion object {
        fun from(
            canWriteSettings: Boolean,
            currentTimeoutMs: Int?,
            canRequestTilePlacement: Boolean,
        ): SetupUiState {
            if (!canWriteSettings) {
                return SetupUiState(
                    primaryAction = SetupAction.GRANT_PERMISSION,
                    timeoutStatus = TimeoutStatus.Unavailable,
                    canRequestTilePlacement = false,
                )
            }

            return SetupUiState(
                primaryAction = if (canRequestTilePlacement) {
                    SetupAction.ADD_TILE
                } else {
                    SetupAction.MANUAL_ADD_TILE
                },
                timeoutStatus = TimeoutStatus.Available(
                    TimeoutDurationLabel.fromMillis(
                        currentTimeoutMs ?: TimeoutTogglePolicy.SHORT_TIMEOUT_MS,
                    ),
                ),
                canRequestTilePlacement = canRequestTilePlacement,
            )
        }
    }
}

enum class SetupAction {
    GRANT_PERMISSION,
    ADD_TILE,
    MANUAL_ADD_TILE,
}

sealed interface TimeoutStatus {
    data object Unavailable : TimeoutStatus
    data class Available(val label: TimeoutDurationLabel) : TimeoutStatus
}
