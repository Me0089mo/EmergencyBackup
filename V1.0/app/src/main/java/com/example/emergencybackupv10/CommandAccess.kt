package com.example.emergencybackupv10

import android.accessibilityservice.AccessibilityButtonController
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class CommandAccess : AccessibilityService() {
    override fun onInterrupt() {
        startIntent()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }

    override fun onServiceConnected() {
        startIntent()
    }

    private fun startIntent(){
        val intent = Intent(this, Login::class.java)
        intent.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        intent.putExtra(getString(R.string.EMERGENCY), true)
        startActivity(intent)
    }
}