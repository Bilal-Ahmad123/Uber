package com.example.uber.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object BitMapCreator {
     fun bitmapDescriptorFromVector(vectorResId: Int, context: Context): BitmapDescriptor? {
        val drawable: Drawable? = ContextCompat.getDrawable(context, vectorResId)
        drawable?.let {
            val originalWidth = it.intrinsicWidth
            val originalHeight = it.intrinsicHeight

            val scaledWidth = (originalWidth * 0.25).toInt()
            val scaledHeight = (originalHeight * 0.25).toInt()

            val bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            it.setBounds(0, 0, canvas.width, canvas.height)
            it.draw(canvas)

            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }
        return null
    }

    fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            30,
            30
        )
        val bitmap = Bitmap.createBitmap(
            30,
            30,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable!!.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}