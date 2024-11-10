package com.example.uber.presentation.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.DisplayMetrics
import android.view.View

object AnimationManager {
    fun startFadeInAnimation(view: View, duration: Long = 500) {
        ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
            this.duration = duration
            start()
        }
    }

    fun animateToEndOfScreenAndScale(
        lineView: View,
        duration: Long = 1000,
        context: Context
    ) {

        val width = context.resources.displayMetrics.widthPixels.toFloat()
        if(lineView.visibility == View.INVISIBLE)
            lineView.visibility = View.VISIBLE
        ObjectAnimator.ofFloat(lineView, "translationX", 0f, width).apply {
            this.duration = duration
            start()
        }
        val scaleXStart = ObjectAnimator.ofFloat(lineView, "scaleX", 0.3f, 1f).apply {
            this.duration = duration / 3
        }

        val scaleXMiddle = ObjectAnimator.ofFloat(lineView, "scaleX", 1f, 0.3f).apply {
            this.duration = duration / 3
        }
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially( scaleXStart,scaleXMiddle)
        animatorSet.start()
    }

}