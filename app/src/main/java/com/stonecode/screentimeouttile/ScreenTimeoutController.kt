package com.stonecode.screentimeouttile

import android.content.Context

class ScreenTimeoutController(
    private val settingsStore: ScreenTimeoutSettingsStore,
    private val policy: TimeoutTogglePolicy = TimeoutTogglePolicy(),
) {

    constructor(context: Context) : this(AndroidScreenTimeoutSettingsStore(context))

    val shortTimeoutMs: Int = policy.shortTimeoutMs
    val longTimeoutMs: Int = policy.longTimeoutMs

    fun canModifySystemSettings(): Boolean = settingsStore.canWrite()

    fun getCurrentTimeout(): Int = settingsStore.getTimeoutMs()

    fun toggleTimeout(): TimeoutChangeResult {
        val current = getCurrentTimeout()
        val newTimeout = policy.nextTimeout(current)
        return if (settingsStore.setTimeoutMs(newTimeout)) {
            TimeoutChangeResult.Changed(newTimeout)
        } else {
            TimeoutChangeResult.WriteFailed
        }
    }
}
