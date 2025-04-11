package com.example.uber.presentation.riderpresentation.bottomSheet

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import com.example.uber.R
import com.example.uber.presentation.riderpresentation.map.utils.ShowNearbyVehicleService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.lang.ref.WeakReference


class VehicleDetailsBottomSheet(
    private val view: WeakReference<View>,
    private val context: Context
) {

    private val bottomSheet: View by lazy {  view.get()!!.findViewById(R.id.vehicle_details_sheet)}
    private val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)


    private fun setBottomSheetStyle() {
        bottomSheet.layoutParams.height =
            (context.resources.displayMetrics.heightPixels * 0.70).toInt()
        bottomSheetBehavior.peekHeight =
            (context.resources.displayMetrics.heightPixels * 0.32).toInt()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isHideable = false
    }

    fun bottomSheetBehaviour(): Int {
        return bottomSheetBehavior.state
    }


    fun showSheet(){
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    init {
        setBottomSheetStyle()
        hideSheet()
    }


    fun hideSheet() {
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

}