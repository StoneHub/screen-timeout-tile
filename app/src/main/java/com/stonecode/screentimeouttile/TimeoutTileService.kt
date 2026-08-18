package com.stonecode.screentimeouttile

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.core.net.toUri

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
                    TimeoutChangeResult.WriteFailed -> showTileError(
                        getString(R.string.tile_subtitle_write_failed),
                    )
                }
            }
            .onFailure {
                showTileError(getString(R.string.tile_subtitle_unavailable))
            }
    }

    @SuppressLint("StartActivityAndCollapseDeprecated")
    private fun requestWriteSettings() {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
            data = "package:$packageName".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching {
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
        }.onFailure {
            showTileError(getString(R.string.tile_subtitle_settings_unavailable))
        }
    }

    private fun refreshTileState() {
        val hasPermission = controller.canModifySystemSettings()
        if (!hasPermission) {
            setTileState(
                state = Tile.STATE_INACTIVE,
                subtitle = getString(R.string.tile_subtitle_permission),
                iconResId = R.drawable.ic_qs_timeout_permission,
            )
        } else {
            runCatching { controller.getCurrentTimeout() }
                .onSuccess(::updateTileForTimeout)
                .onFailure {
                    setTileState(
                        state = Tile.STATE_UNAVAILABLE,
                        subtitle = getString(R.string.tile_subtitle_unavailable),
                        iconResId = R.drawable.ic_qs_timeout_long,
                    )
                }
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
        tile.label = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || subtitle == null) {
            getString(R.string.tile_label)
        } else {
            subtitle
        }
        tile.icon = Icon.createWithResource(this, iconResId)
        tile.contentDescription = getString(R.string.tile_content_description)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = subtitle
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            tile.stateDescription = subtitle
        }
        tile.updateTile()
    }

    private fun showTileError(message: String) {
        setTileState(
            state = Tile.STATE_UNAVAILABLE,
            subtitle = message,
            iconResId = R.drawable.ic_qs_timeout_permission,
        )
    }
}
