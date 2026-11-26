package com.stonecode.screentimeouttile

/**
 * What the home-screen widget should show. Kept free of Android types so the
 * mapping from permission and timeout to visuals is unit-testable.
 */
sealed interface TimeoutWidgetState {
    data object NeedsPermission : TimeoutWidgetState
    data object Unavailable : TimeoutWidgetState
    data class Timeout(
        val label: TimeoutDurationLabel,
        val visualState: TimeoutTileVisualState,
    ) : TimeoutWidgetState

    companion object {
        fun from(
            canWriteSettings: Boolean,
            currentTimeoutMs: Int?,
            policy: TimeoutTogglePolicy = TimeoutTogglePolicy(),
        ): TimeoutWidgetState = when {
            !canWriteSettings -> NeedsPermission
            currentTimeoutMs == null -> Unavailable
            else -> Timeout(
                label = TimeoutDurationLabel.fromMillis(currentTimeoutMs),
                visualState = policy.visualStateFor(currentTimeoutMs),
            )
        }
    }
}
