package com.stonecode.screentimeouttile

import android.content.Context

object TimeoutLabelFormatter {

    fun format(context: Context, timeoutMs: Int): String {
        val seconds = timeoutMs / 1000
        return if (seconds >= 60) {
            val minutes = seconds / 60
            context.getString(R.string.tile_subtitle_minutes, minutes)
        } else {
            context.getString(R.string.tile_subtitle_seconds, seconds)
        }
    }
}
