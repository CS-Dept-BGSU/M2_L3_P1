package com.example.m2_l3_p1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var messageEditText: EditText
    private lateinit var sendNotificationButton: Button

    // Permission request launcher
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, enable notification functionality
            sendNotificationButton.isEnabled = true
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            // Permission denied, show explanation
            sendNotificationButton.isEnabled = false
            showPermissionRationaleDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        messageEditText = findViewById(R.id.editTextMessage)
        sendNotificationButton = findViewById(R.id.buttonSendNotification)

        // Check and request notification permission
        checkNotificationPermission()

        // Set up button listener
        sendNotificationButton.setOnClickListener {
            sendNotification()
        }
    }

    private fun checkNotificationPermission() {
        // Only request permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                    sendNotificationButton.isEnabled = true
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show an explanation to the user
                    showPermissionRationaleDialog()
                }
                else -> {
                    // Directly ask for the permission
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // For Android versions below 13, permission is granted by default
            sendNotificationButton.isEnabled = true
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notification Permission Required")
            .setMessage("This app needs notification permission to send notifications. Open app settings to grant this permission?")
            .setPositiveButton("Open Settings") { _, _ ->
                // Open app settings
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "Notification functionality will be disabled", Toast.LENGTH_SHORT).show()
            }
            .create()
            .show()
    }

    private fun sendNotification() {
        val message = messageEditText.text.toString()

        // Only send notification if permission is granted
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED) {

            // Start service to send notification
            val intent = Intent(this, MainService::class.java).apply {
                putExtra("message", message)
            }
            startService(intent)
        } else {
            Toast.makeText(this, "Notification permission not granted", Toast.LENGTH_SHORT).show()
            checkNotificationPermission()
        }
    }
}