package com.example.uber.presentation.bottomSheet

import android.app.Activity
import android.content.Context
import android.view.View
import com.example.uber.R
import com.example.uber.presentation.map.RouteCreationHelper
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class RideOptionsBottomSheet private constructor(
    private val view: View, private val context: Context,
) {
    private val bottomSheet: View = view.findViewById(R.id.ride_options_bottom_sheet)
    private val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
    private val shimmer: ShimmerFrameLayout by lazy { view.findViewById(R.id.shimmerLayout) }

    companion object{
        @Volatile
        private var instance:RideOptionsBottomSheet? = null
        fun initialize( view: View,context: Context):RideOptionsBottomSheet?{
            if(instance == null){
                synchronized(this){
                    if(instance == null){
                        instance = RideOptionsBottomSheet(view,context)
                    }
                }
            }
            return instance
        }

        fun getInstance():RideOptionsBottomSheet?{
            return instance
        }


        fun cleanResources(){
            if (instance != null){
                instance = null
            }
        }
    }

    init {
        setBottomSheetStyle()
        initialBottomSheetHidden()
        setupBottomSheetCallback()
    }

    private fun setupBottomSheetCallback() {
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                handleStateChange(newState)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }

    private fun handleStateChange(newState: Int) {

        when (newState) {
            BottomSheetBehavior.STATE_EXPANDED -> {
                RouteCreationHelper.getInstance()?.animateToRespectivePadding(1000)
            }

            BottomSheetBehavior.STATE_COLLAPSED -> {
                RouteCreationHelper.getInstance()?.animateToRespectivePadding()
            }
        }
    }

    private fun setBottomSheetStyle() {
        bottomSheet.layoutParams.height =
            (context.resources.displayMetrics.heightPixels * 0.70).toInt()
        bottomSheetBehavior.peekHeight =
            (context.resources.displayMetrics.heightPixels * 0.32).toInt()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.isHideable = false
    }

    fun showBottomSheet() {
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        shimmer.startShimmer()
    }

    private fun initialBottomSheetHidden() {
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun hideBottomSheet() {
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun bottomSheetBehaviour(): Int {
        return bottomSheetBehavior.state
    }
}