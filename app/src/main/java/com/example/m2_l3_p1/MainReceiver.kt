package com.example.m2_l3_p1

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class MainReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_NOTIFICATION_SENT -> {
                val message = intent.getStringExtra("message") ?: "No message"

                // Log the notification
                Log.d(TAG, "Notification sent: $message")

                // Show a toast to confirm
                Toast.makeText(context, "Notification Processed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val TAG = "NotificationReceiver"
        const val ACTION_NOTIFICATION_SENT = "com.example.m2_l3_p1.NOTIFICATION_SENT"
    }
}