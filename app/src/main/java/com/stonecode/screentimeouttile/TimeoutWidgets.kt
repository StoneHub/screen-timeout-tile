package com.stonecode.screentimeouttile

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import android.util.SizeF
import android.widget.RemoteViews
import androidx.core.content.ContextCompat

/** Renders every placed widget and keeps the widget and tile in step. */
object TimeoutWidgets {

    private const val WIDE_MIN_WIDTH_DP = 130

    fun updateAll(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, TimeoutWidgetProvider::class.java))
        if (ids.isEmpty()) return
        update(context, manager, ids)
    }

    fun update(context: Context, manager: AppWidgetManager, ids: IntArray) {
        val state = currentState(context)
        ids.forEach { id -> manager.updateAppWidget(id, remoteViewsFor(context, manager, id, state)) }
    }

    fun hasWidgets(context: Context): Boolean = AppWidgetManager.getInstance(context)
        .getAppWidgetIds(ComponentName(context, TimeoutWidgetProvider::class.java))
        .isNotEmpty()

    /** Ask the Quick Settings tile to re-read the timeout the next time it is visible. */
    fun refreshTile(context: Context) {
        runCatching {
            TileService.requestListeningState(
                context,
                ComponentName(context, TimeoutTileService::class.java),
            )
        }
    }

    private fun currentState(context: Context): TimeoutWidgetState {
        val controller = ScreenTimeoutController(context)
        val canWrite = controller.canModifySystemSettings()
        val timeout = if (canWrite) runCatching { controller.getCurrentTimeout() }.getOrNull() else null
        return TimeoutWidgetState.from(canWrite, timeout)
    }

    private fun remoteViewsFor(
        context: Context,
        manager: AppWidgetManager,
        widgetId: Int,
        state: TimeoutWidgetState,
    ): RemoteViews {
        val small = render(context, R.layout.widget_timeout_small, state)
        val wide = render(context, R.layout.widget_timeout_wide, state)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return RemoteViews(
                mapOf(
                    SizeF(0f, 0f) to small,
                    SizeF(WIDE_MIN_WIDTH_DP.toFloat(), 0f) to wide,
                ),
            )
        }
        val minWidth = manager.getAppWidgetOptions(widgetId)
            .getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0)
        return if (minWidth >= WIDE_MIN_WIDTH_DP) wide else small
    }

    private fun render(context: Context, layout: Int, state: TimeoutWidgetState): RemoteViews {
        val views = RemoteViews(context.packageName, layout)
        val active = state is TimeoutWidgetState.Timeout &&
            state.visualState == TimeoutTileVisualState.ACTIVE
        val foreground = ContextCompat.getColor(
            context,
            if (active) R.color.widget_on_active else R.color.widget_on_surface,
        )
        val secondary = ContextCompat.getColor(
            context,
            if (active) R.color.widget_on_active_secondary else R.color.widget_on_surface_secondary,
        )
        val (icon, value) = when (state) {
            TimeoutWidgetState.NeedsPermission ->
                R.drawable.ic_qs_timeout_permission to context.getString(R.string.widget_value_permission)
            TimeoutWidgetState.Unavailable ->
                R.drawable.ic_qs_timeout_permission to context.getString(R.string.widget_value_unavailable)
            is TimeoutWidgetState.Timeout -> {
                val iconRes = if (active) R.drawable.ic_qs_timeout_long else R.drawable.ic_qs_timeout_short
                iconRes to TimeoutLabelFormatter.formatShort(context, state.label)
            }
        }
        views.setInt(
            R.id.widgetRoot,
            "setBackgroundResource",
            if (active) R.drawable.widget_background_active else R.drawable.widget_background,
        )
        views.setImageViewResource(R.id.widgetIcon, icon)
        views.setInt(R.id.widgetIcon, "setColorFilter", foreground)
        views.setTextViewText(R.id.widgetValue, value)
        views.setTextColor(R.id.widgetValue, foreground)
        views.setTextColor(R.id.widgetLabel, secondary)
        views.setContentDescription(
            R.id.widgetRoot,
            context.getString(R.string.widget_content_description, value),
        )
        views.setOnClickPendingIntent(R.id.widgetRoot, tapIntent(context, state))
        return views
    }

    private fun tapIntent(context: Context, state: TimeoutWidgetState): PendingIntent =
        if (state is TimeoutWidgetState.NeedsPermission) {
            // Setup explains the permission and refreshes widgets when the user returns.
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        } else {
            PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, TimeoutWidgetToggleReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }
}
