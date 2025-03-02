package com.example.uber.core.interfaces.utils.mode

import android.content.Context
import android.content.res.Configuration

object CheckMode {
    fun isDarkMode(context: Context): Boolean {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    fun checkCurrentMode(context: Context){

    }
}