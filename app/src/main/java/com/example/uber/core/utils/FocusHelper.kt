package com.example.uber.core.utils

import android.widget.TextView

object FocusHelper {
    fun isInputInFocus(pickupField: TextView, dropOffField: TextView): Pair<Boolean, Boolean> {
        val isPickupInFocus = pickupField.hasFocus()
        val isDropOffInFocus = dropOffField.hasFocus()
        return Pair(isPickupInFocus, isDropOffInFocus)
    }
}