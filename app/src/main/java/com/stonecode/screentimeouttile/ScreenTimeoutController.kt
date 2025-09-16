package com.stonecode.screentimeouttile

import android.content.ContentResolver
import android.content.Context
import android.provider.Settings

private const val DEFAULT_SHORT_TIMEOUT_MS = 30_000
private const val DEFAULT_LONG_TIMEOUT_MS = 600_000

class ScreenTimeoutController(private val context: Context) {

    private val resolver: ContentResolver = context.contentResolver

    val shortTimeoutMs: Int = DEFAULT_SHORT_TIMEOUT_MS
    val longTimeoutMs: Int = DEFAULT_LONG_TIMEOUT_MS

    fun canModifySystemSettings(): Boolean = Settings.System.canWrite(context)

    fun getCurrentTimeout(): Int = Settings.System.getInt(
        resolver,
        Settings.System.SCREEN_OFF_TIMEOUT,
        shortTimeoutMs
    )

    fun toggleTimeout(): Int {
        val current = getCurrentTimeout()
        val newTimeout = if (current > shortTimeoutMs) {
            shortTimeoutMs
        } else {
            longTimeoutMs
        }
        Settings.System.putInt(resolver, Settings.System.SCREEN_OFF_TIMEOUT, newTimeout)
        return newTimeout
    }
}
