package com.stonecode.screentimeouttile

import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.progressindicator.LinearProgressIndicator

class MainActivity : AppCompatActivity() {

    private lateinit var controller: ScreenTimeoutController
    private lateinit var statusView: TextView
    private lateinit var timeoutView: TextView
    private lateinit var permissionRationaleView: TextView
    private lateinit var nextActionView: TextView
    private lateinit var actionButton: Button
    private lateinit var tileButton: Button
    private lateinit var tileRequestStatusView: TextView
    private lateinit var permissionStepBadge: TextView
    private lateinit var tileStepBadge: TextView
    private lateinit var useStepBadge: TextView
    private lateinit var setupProgress: LinearProgressIndicator

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
        permissionRationaleView = findViewById(R.id.permissionRationale)
        nextActionView = findViewById(R.id.nextAction)
        actionButton = findViewById(R.id.permissionButton)
        tileButton = findViewById(R.id.tileButton)
        tileRequestStatusView = findViewById(R.id.tileRequestStatus)
        permissionStepBadge = findViewById(R.id.permissionStepBadge)
        tileStepBadge = findViewById(R.id.tileStepBadge)
        useStepBadge = findViewById(R.id.useStepBadge)
        setupProgress = findViewById(R.id.setupProgress)

        actionButton.setOnClickListener { openWriteSettings() }
        tileButton.setOnClickListener { requestTilePlacement() }
    }

    private fun configureSystemBars() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
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

        timeoutView.text = when (val status = state.timeoutStatus) {
            is TimeoutStatus.Available -> getString(
                R.string.current_timeout_value,
                TimeoutLabelFormatter.format(this, status.label),
            )
            TimeoutStatus.Unavailable -> getString(R.string.current_timeout_unknown)
        }

        renderSetupState(state)
    }

    private fun renderSetupState(state: SetupUiState) {
        val needsPermission = state.primaryAction == SetupAction.GRANT_PERMISSION

        if (needsPermission) {
            setupProgress.setProgressCompat(PROGRESS_PERMISSION, true)
            nextActionView.text = getString(R.string.setup_progress_step_one)
            statusView.text = getString(R.string.setup_permission_title)
            permissionRationaleView.visibility = View.VISIBLE
            timeoutView.visibility = View.GONE
            actionButton.visibility = View.VISIBLE
            actionButton.isEnabled = true
            tileButton.visibility = View.VISIBLE
            tileButton.isEnabled = false
            tileRequestStatusView.text = getString(R.string.setup_tile_locked_description)
            renderBadge(permissionStepBadge, "1", R.drawable.setup_step_badge_current, R.color.white)
            renderBadge(tileStepBadge, "2", R.drawable.setup_step_badge_pending, R.color.setup_text_muted)
            renderBadge(useStepBadge, "3", R.drawable.setup_step_badge_pending, R.color.setup_text_muted)
            return
        }

        setupProgress.setProgressCompat(PROGRESS_TILE, true)
        nextActionView.text = getString(R.string.setup_progress_step_two)
        statusView.text = getString(R.string.setup_permission_complete_title)
        permissionRationaleView.visibility = View.GONE
        timeoutView.visibility = View.VISIBLE
        actionButton.visibility = View.GONE
        renderBadge(permissionStepBadge, CHECK_MARK, R.drawable.setup_step_badge_done, R.color.white)
        renderBadge(tileStepBadge, "2", R.drawable.setup_step_badge_current, R.color.white)
        renderBadge(useStepBadge, "3", R.drawable.setup_step_badge_pending, R.color.setup_text_muted)

        if (state.canRequestTilePlacement) {
            tileButton.visibility = View.VISIBLE
            tileButton.isEnabled = true
            tileRequestStatusView.text = getString(R.string.setup_tile_ready_description)
        } else {
            tileButton.visibility = View.GONE
            tileRequestStatusView.text = getString(R.string.setup_tile_manual_description)
        }
    }

    private fun renderBadge(
        badge: TextView,
        label: String,
        backgroundResource: Int,
        textColorResource: Int,
    ) {
        badge.text = label
        badge.setBackgroundResource(backgroundResource)
        badge.setTextColor(ContextCompat.getColor(this, textColorResource))
    }

    private fun renderSetupComplete() {
        setupProgress.setProgressCompat(PROGRESS_COMPLETE, true)
        nextActionView.text = getString(R.string.setup_progress_complete)
        tileButton.isEnabled = false
        renderBadge(tileStepBadge, CHECK_MARK, R.drawable.setup_step_badge_done, R.color.white)
        renderBadge(useStepBadge, CHECK_MARK, R.drawable.setup_step_badge_done, R.color.white)
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
                if (isTilePlacementComplete(result)) {
                    renderSetupComplete()
                }
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

    private fun isTilePlacementComplete(result: Int): Boolean =
        result == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED ||
            result == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED

    companion object {
        private const val CHECK_MARK = "✓"
        private const val PROGRESS_PERMISSION = 33
        private const val PROGRESS_TILE = 67
        private const val PROGRESS_COMPLETE = 100
    }
}
