package com.example.uber.presentation.riderpresentation.bottomSheet

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.uber.R
import com.example.uber.presentation.riderpresentation.viewModels.RiderViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.lang.ref.WeakReference

class RideOptionsBottomSheet(
    private val view: WeakReference<View>, private val context: Context,
    private val viewModelStoreOwner : ViewModelStoreOwner
) {
    private val bottomSheet: View = view.get()!!.findViewById(R.id.ride_options_bottom_sheet)
    private val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
    private val shimmer: ShimmerFrameLayout by lazy { view.get()!!.findViewById(R.id.shimmerLayout) }

    init {
        setBottomSheetStyle()
        initialBottomSheetHidden()
        setupBottomSheetCallback()
    }

    private val riderViewModel : RiderViewModel by lazy {
        ViewModelProvider(viewModelStoreOwner)[RiderViewModel::class.java]
    }

    private val

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
//                RouteCreationHelper.getInstance()?.animateToRespectivePadding(1000)
            }

            BottomSheetBehavior.STATE_COLLAPSED -> {
//                RouteCreationHelper.getInstance()?.animateToRespectivePadding()
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

    private fun showNearbyVehicles(){

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

    private fun getNearbyVehicles(){
        riderViewModel.getNearbyVehicles()
    }
}