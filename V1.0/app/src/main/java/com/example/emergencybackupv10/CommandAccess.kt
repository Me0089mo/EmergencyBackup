package com.example.emergencybackupv10

import android.accessibilityservice.AccessibilityButtonController
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi

class CommandAccess : AccessibilityService() {
    override fun onInterrupt() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    private var accessBtnController: AccessibilityButtonController? = null
    private var accessBtnCallback:
            AccessibilityButtonController.AccessibilityButtonCallback? = null
    private var accessBtnAvailable: Boolean = false

    /*
    override fun onServiceConnected() {
        info.apply {
            // Set the type of events that this service wants to listen to. Others
            // won't be passed to this service.
            eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED or AccessibilityEvent.TYPE_VIEW_FOCUSED

            // If you only want this service to work with specific applications, set their
            // package names here. Otherwise, when the service is activated, it will listen
            // to events from all applications.
            packageNames = arrayOf("com.example.android.myFirstApp", "com.example.android.mySecondApp")

            // Set the type of feedback your service will provide.
            feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN

            // Default services are invoked only if no package-specific ones are present
            // for the type of AccessibilityEvent generated. This service *is*
            // application-specific, so the flag isn't necessary. If this was a
            // general-purpose service, it would be worth considering setting the
            // DEFAULT flag.

            // flags = AccessibilityServiceInfo.DEFAULT;

            notificationTimeout = 100
        }

        this.serviceInfo = info

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onServiceConnected() {

        accessBtnController = accessibilityButtonController
        accessBtnAvailable = accessBtnController?.isAccessibilityButtonAvailable!!
        if (!accessBtnAvailable) return
        serviceInfo = serviceInfo.apply {
            flags = flags or AccessibilityServiceInfo.FLAG_REQUEST_ACCESSIBILITY_BUTTON
        }
        accessBtnCallback =
                object : AccessibilityButtonController.AccessibilityButtonCallback() {
                    override fun onClicked(controller: AccessibilityButtonController) {
                        Log.d("MY_APP_TAG", "Accessibility button pressed!")

                        // Add custom logic for a service to react to the
                        // accessibility button being pressed.
                    }

                    override fun onAvailabilityChanged(
                            controller: AccessibilityButtonController,
                            available: Boolean
                    ) {
                        if (controller == accessBtnController) {
                            accessBtnAvailable = available
                        }
                    }
                }

        accessBtnCallback?.also {
            accessBtnController?.registerAccessibilityButtonCallback(it, null)
        }
    }
    */
}