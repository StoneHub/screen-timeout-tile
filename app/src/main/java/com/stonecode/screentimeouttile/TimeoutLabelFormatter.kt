package com.stonecode.screentimeouttile

import android.content.Context

object TimeoutLabelFormatter {

    fun format(context: Context, timeoutMs: Int): String {
        return format(context, TimeoutDurationLabel.fromMillis(timeoutMs))
    }

    fun format(context: Context, label: TimeoutDurationLabel): String {
        return when (label) {
            is TimeoutDurationLabel.Minutes -> context.resources.getQuantityString(
                R.plurals.tile_subtitle_minutes,
                label.value,
                label.value,
            )
            is TimeoutDurationLabel.Seconds -> context.resources.getQuantityString(
                R.plurals.tile_subtitle_seconds,
                label.value,
                label.value,
            )
        }
    }

    /** Compact label for small surfaces such as the home-screen widget, e.g. "10 min". */
    fun formatShort(context: Context, label: TimeoutDurationLabel): String {
        return when (label) {
            is TimeoutDurationLabel.Minutes -> context.getString(R.string.widget_value_minutes, label.value)
            is TimeoutDurationLabel.Seconds -> context.getString(R.string.widget_value_seconds, label.value)
        }
    }
}
