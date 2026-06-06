package com.stonecode.screentimeouttile

import android.content.ContentResolver
import android.content.Context
import android.provider.Settings

interface ScreenTimeoutSettingsStore {
    fun canWrite(): Boolean
    fun getTimeoutMs(): Int
    fun setTimeoutMs(timeoutMs: Int): Boolean
}

class AndroidScreenTimeoutSettingsStore(context: Context) : ScreenTimeoutSettingsStore {

    private val appContext = context.applicationContext
    private val resolver: ContentResolver = appContext.contentResolver

    override fun canWrite(): Boolean = Settings.System.canWrite(appContext)

    override fun getTimeoutMs(): Int = Settings.System.getInt(
        resolver,
        Settings.System.SCREEN_OFF_TIMEOUT,
        TimeoutTogglePolicy.SHORT_TIMEOUT_MS,
    )

    override fun setTimeoutMs(timeoutMs: Int): Boolean = Settings.System.putInt(
        resolver,
        Settings.System.SCREEN_OFF_TIMEOUT,
        timeoutMs,
    )
}
