package com.example.uber.presentation.bottomSheet

import android.content.Context
import android.view.View
import com.example.uber.R
import com.faltenreich.skeletonlayout.Skeleton
import com.google.android.material.bottomsheet.BottomSheetBehavior

class RideOptionsBottomSheet(
    private val view: View, private val context: Context,
) {
    private val bottomSheet: View = view.findViewById(R.id.ride_options_bottom_sheet)
    private val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
    private val skeleton:Skeleton =  bottomSheet.findViewById(R.id.skeletonLayout)

    init {
        setBottomSheetStyle()
        initialBottomSheetHidden()
    }

    private fun setBottomSheetStyle() {
        bottomSheet.layoutParams.height =
            (context.resources.displayMetrics.heightPixels * 0.80).toInt()
        bottomSheetBehavior.peekHeight =
            (context.resources.displayMetrics.heightPixels * 0.32).toInt()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.isHideable = false
    }

    fun showBottomSheet(){
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        skeleton.showShimmer = true
        skeleton.showSkeleton()
    }

    private fun initialBottomSheetHidden(){
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun hideBottomSheet(){
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }
}