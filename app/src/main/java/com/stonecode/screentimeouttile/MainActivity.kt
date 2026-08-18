package com.stonecode.screentimeouttile

import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class MainActivity : AppCompatActivity() {

    private lateinit var controller: ScreenTimeoutController
    private lateinit var statusView: TextView
    private lateinit var timeoutView: TextView
    private lateinit var nextActionView: TextView
    private lateinit var actionButton: Button
    private lateinit var tileButton: Button
    private lateinit var tileRequestStatusView: TextView

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        updateUi()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.enableEdgeToEdge(window)
        setContentView(R.layout.activity_main)
        ViewCompat.setAccessibilityHeading(findViewById(R.id.title), true)
        configureSystemBars()

        controller = ScreenTimeoutController(this)
        statusView = findViewById(R.id.permissionStatus)
        timeoutView = findViewById(R.id.timeoutValue)
        nextActionView = findViewById(R.id.nextAction)
        actionButton = findViewById(R.id.permissionButton)
        tileButton = findViewById(R.id.tileButton)
        tileRequestStatusView = findViewById(R.id.tileRequestStatus)

        actionButton.setOnClickListener { openWriteSettings() }
        tileButton.setOnClickListener { requestTilePlacement() }
    }

    private fun configureSystemBars() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.screenContent)) { view, insets ->
            val safeDrawing = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
            )
            view.updatePadding(
                left = safeDrawing.left,
                top = safeDrawing.top,
                right = safeDrawing.right,
                bottom = safeDrawing.bottom,
            )
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        updateUi()
    }

    private fun openWriteSettings() {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
            data = "package:$packageName".toUri()
        }
        runCatching { permissionLauncher.launch(intent) }
            .onFailure {
                nextActionView.text = getString(R.string.permission_request_status_error)
            }
    }

    private fun updateUi() {
        val hasPermission = controller.canModifySystemSettings()
        val timeout = if (hasPermission) {
            runCatching { controller.getCurrentTimeout() }.getOrNull()
        } else {
            null
        }
        val state = SetupUiState.from(
            canWriteSettings = hasPermission,
            currentTimeoutMs = timeout,
            canRequestTilePlacement = canRequestTilePlacement(),
        )

        statusView.text = if (hasPermission) {
            getString(R.string.permission_granted_status)
        } else {
            getString(R.string.permission_needed_status)
        }

        actionButton.isEnabled = state.primaryAction == SetupAction.GRANT_PERMISSION
        tileButton.isEnabled = state.canRequestTilePlacement
        nextActionView.text = when (state.primaryAction) {
            SetupAction.GRANT_PERMISSION -> getString(R.string.next_action_grant_permission)
            SetupAction.ADD_TILE -> getString(R.string.next_action_add_tile)
            SetupAction.MANUAL_ADD_TILE -> getString(R.string.next_action_manual_add_tile)
        }

        if (!state.canRequestTilePlacement && hasPermission) {
            tileRequestStatusView.text = getString(R.string.tile_request_status_unsupported)
        }

        timeoutView.text = when (val status = state.timeoutStatus) {
            is TimeoutStatus.Available -> getString(
                R.string.current_timeout_value,
                TimeoutLabelFormatter.format(this, status.label),
            )
            TimeoutStatus.Unavailable -> getString(R.string.current_timeout_unknown)
        }
    }

    private fun canRequestTilePlacement(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    private fun requestTilePlacement() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            tileRequestStatusView.text = getString(R.string.tile_request_status_unsupported)
            return
        }

        runCatching {
            val statusBarManager = getSystemService(StatusBarManager::class.java)
            val componentName = ComponentName(this, TimeoutTileService::class.java)
            val icon = Icon.createWithResource(this, R.drawable.ic_qs_timeout_long)
            statusBarManager.requestAddTileService(
                componentName,
                getString(R.string.tile_label),
                icon,
                mainExecutor,
            ) { result ->
                tileRequestStatusView.text = tileRequestStatusText(result)
            }
        }.onFailure {
            tileRequestStatusView.text = getString(R.string.tile_request_status_error)
        }
    }

    private fun tileRequestStatusText(result: Int): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        when (result) {
            StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED -> {
                getString(R.string.tile_request_status_added)
            }
            StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED -> {
                getString(R.string.tile_request_status_already_added)
            }
            StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_NOT_ADDED -> {
                getString(R.string.tile_request_status_not_added)
            }
            else -> getString(R.string.tile_request_status_error)
        }
    } else {
        getString(R.string.tile_request_status_unsupported)
    }
}
