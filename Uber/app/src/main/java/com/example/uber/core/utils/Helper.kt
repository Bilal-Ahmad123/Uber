package com.example.uber.core.utils

import android.animation.ValueAnimator
import android.view.View
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

object Helper {
     fun calculateBounds(latLngBounds:List<LatLng>?): LatLngBounds? {
        val builder = LatLngBounds.Builder()
        val latLngList = latLngBounds

        if (latLngList == null) {
            return null
        }

        latLngList?.forEach {
            builder.include(it)
        }

        return builder.build()
    }


    fun animatePinWidth(view: View, startWidth: Int, endWidth: Int, duration: Long = 500L){
        val animator = ValueAnimator.ofInt(startWidth, endWidth)
        animator.duration = duration
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val layoutParams = view.layoutParams
            layoutParams.width = value
            view.layoutParams = layoutParams
        }

        animator.start()
    }

    fun calculateSheetOffSet(height: Int, peekHeight: Int,sheetTop:Int):Float {
        val collapsedY = height - peekHeight
        val expandedY = 0f // top of screen

        val currentOffset = height - sheetTop

        val normalizedOffset = (collapsedY - sheetTop) / (collapsedY - expandedY)
        return normalizedOffset

    }
}