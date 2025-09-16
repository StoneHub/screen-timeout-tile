package com.stonecode.screentimeouttile

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
            .onSuccess { updatedTimeout ->
                updateTileForTimeout(updatedTimeout)
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
        startActivityAndCollapse(intent)
    }

    private fun refreshTileState() {
        val hasPermission = controller.canModifySystemSettings()
        val timeout = runCatching { controller.getCurrentTimeout() }.getOrDefault(controller.shortTimeoutMs)
        if (!hasPermission) {
            setTileState(Tile.STATE_INACTIVE, getString(R.string.tile_subtitle_permission))
        } else {
            updateTileForTimeout(timeout)
        }
    }

    private fun updateTileForTimeout(timeoutMs: Int) {
        val state = if (timeoutMs > controller.shortTimeoutMs) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        val subtitle = TimeoutLabelFormatter.format(this, timeoutMs)
        setTileState(state, subtitle)
    }

    private fun setTileState(state: Int, subtitle: String?) {
        val tile = qsTile ?: return
        tile.state = state
        tile.label = getString(R.string.app_name)
        tile.icon = Icon.createWithResource(this, R.drawable.ic_qs_timeout)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = subtitle
        } else {
            tile.contentDescription = subtitle ?: getString(R.string.app_description)
        }
        tile.updateTile()
    }
}
