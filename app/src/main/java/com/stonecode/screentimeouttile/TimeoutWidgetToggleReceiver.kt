package com.stonecode.screentimeouttile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Not exported: only the widget's own PendingIntent can toggle the timeout. */
class TimeoutWidgetToggleReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val controller = ScreenTimeoutController(context)
        if (controller.canModifySystemSettings()) {
            runCatching { controller.toggleTimeout() }
        }
        TimeoutWidgets.updateAll(context)
        TimeoutWidgets.refreshTile(context)
    }
}
