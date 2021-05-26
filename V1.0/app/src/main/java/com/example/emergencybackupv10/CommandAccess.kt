package com.example.emergencybackupv10

import android.accessibilityservice.AccessibilityButtonController
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class CommandAccess : AccessibilityService() {
    private var mserviceInfo: AccessibilityServiceInfo? = null
    override fun onInterrupt() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        println(event?.eventTime?.dec())
    }

    /*
    private var accessBtnController: AccessibilityButtonController? = null
    private var accessBtnCallback:
            AccessibilityButtonController.AccessibilityButtonCallback? = null
    private var accessBtnAvailable: Boolean = false
    */

    override fun onServiceConnected() {
        val info = AccessibilityServiceInfo()
        info.apply {
            // Set the type of events that this service wants to listen to. Others
            // won't be passed to this service.
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            // If you only want this service to work with specific applications, set their
            // package names here. Otherwise, when the service is activated, it will listen
            // to events from all applications.
            packageNames = arrayOf("com.example.android.emergencybackupv10")

            // Set the type of feedback your service will provide.
            feedbackType = AccessibilityServiceInfo.FEEDBACK_VISUAL

            // Default services are invoked only if no package-specific ones are present
            // for the type of AccessibilityEvent generated. This service *is*
            // application-specific, so the flag isn't necessary. If this was a
            // general-purpose service, it would be worth considering setting the
            // DEFAULT flag.

            flags = AccessibilityServiceInfo.DEFAULT;

            notificationTimeout = 100
        }

        mserviceInfo = info
    }
}