package com.stonecode.screentimeouttile

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
class TimeoutTileService : TileService() {

    private val controller by lazy { ScreenTimeoutController(this) }

    override fun onStartListening() {
        super.onStartListening()
        refreshTileState()
    }

    override fun onClick() {
        super.onClick()
        if (!controller.canModifySystemSettings()) {
            requestWriteSettings()
            return
        }

        runCatching { controller.toggleTimeout() }
            .onSuccess { result ->
                when (result) {
                    is TimeoutChangeResult.Changed -> updateTileForTimeout(result.timeoutMs)
                    TimeoutChangeResult.WriteFailed -> refreshTileState()
                }
            }
            .onFailure {
                refreshTileState()
            }
    }

    private fun requestWriteSettings() {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            startActivityAndCollapse(pendingIntent)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }

    private fun refreshTileState() {
        val hasPermission = controller.canModifySystemSettings()
        val timeout = runCatching { controller.getCurrentTimeout() }.getOrDefault(controller.shortTimeoutMs)
        if (!hasPermission) {
            setTileState(
                state = Tile.STATE_INACTIVE,
                subtitle = getString(R.string.tile_subtitle_permission),
                iconResId = R.drawable.ic_qs_timeout_permission,
            )
        } else {
            updateTileForTimeout(timeout)
        }
    }

    private fun updateTileForTimeout(timeoutMs: Int) {
        val state = when (TimeoutTogglePolicy().visualStateFor(timeoutMs)) {
            TimeoutTileVisualState.ACTIVE -> Tile.STATE_ACTIVE
            TimeoutTileVisualState.INACTIVE -> Tile.STATE_INACTIVE
        }
        val subtitle = TimeoutLabelFormatter.format(this, timeoutMs)
        val iconResId = if (state == Tile.STATE_ACTIVE) {
            R.drawable.ic_qs_timeout_long
        } else {
            R.drawable.ic_qs_timeout_short
        }
        setTileState(state, subtitle, iconResId)
    }

    private fun setTileState(state: Int, subtitle: String?, iconResId: Int) {
        val tile = qsTile ?: return
        tile.state = state
        tile.label = getString(R.string.app_name)
        tile.icon = Icon.createWithResource(this, iconResId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = subtitle
        } else {
            tile.contentDescription = subtitle ?: getString(R.string.app_description)
        }
        tile.updateTile()
    }
}
