package com.adx.aiassistant

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class AdxAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
}
