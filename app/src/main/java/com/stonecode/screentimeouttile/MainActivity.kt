package com.stonecode.screentimeouttile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var controller: ScreenTimeoutController
    private lateinit var statusView: TextView
    private lateinit var timeoutView: TextView
    private lateinit var actionButton: Button

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        updateUi()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        controller = ScreenTimeoutController(this)
        statusView = findViewById(R.id.permissionStatus)
        timeoutView = findViewById(R.id.timeoutValue)
        actionButton = findViewById(R.id.permissionButton)

        actionButton.setOnClickListener { openWriteSettings() }
    }

    override fun onResume() {
        super.onResume()
        updateUi()
    }

    private fun openWriteSettings() {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        }
        permissionLauncher.launch(intent)
    }

    private fun updateUi() {
        val hasPermission = controller.canModifySystemSettings()
        statusView.text = if (hasPermission) {
            getString(R.string.permission_granted_status)
        } else {
            getString(R.string.permission_needed_status)
        }

        actionButton.isEnabled = !hasPermission

        timeoutView.text = if (hasPermission) {
            val timeout = controller.getCurrentTimeout()
            getString(
                R.string.current_timeout_value,
                TimeoutLabelFormatter.format(this, timeout)
            )
        } else {
            getString(R.string.current_timeout_unknown)
        }
    }
}
