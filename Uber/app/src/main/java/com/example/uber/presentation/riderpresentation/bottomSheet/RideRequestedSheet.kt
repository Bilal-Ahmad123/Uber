package com.example.uber.presentation.riderpresentation.bottomSheet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import com.example.uber.R
import com.example.uber.core.utils.Helper
import com.example.uber.presentation.riderpresentation.map.Routes.RouteCreationHelper
import com.example.uber.presentation.riderpresentation.viewModels.MapAndSheetsSharedViewModel
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.bottomsheet.BottomSheetBehavior

class RideRequestedSheet : Fragment(R.layout.fragment_ride_requested_sheet) {
    private val sharedViewModel: MapAndSheetsSharedViewModel by activityViewModels<MapAndSheetsSharedViewModel>()
    private var bottomSheet: LinearLayout? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ride_requested_sheet, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomSheetStyle()
        setupBottomSheetCallback()

    }

    private var bounds: LatLngBounds? = null
    private fun setBottomSheetStyle() {
        bottomSheet = requireActivity().findViewById<LinearLayout>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)

        bottomSheet?.layoutParams?.height =
            (requireContext().resources.displayMetrics.heightPixels * 0.70).toInt()
        bottomSheetBehavior?.peekHeight =
            (requireContext().resources.displayMetrics.heightPixels * 0.32).toInt()
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior?.isHideable = false
        bottomSheetBehavior?.isDraggable = true
    }

    private fun setupBottomSheetCallback() {
        bottomSheetBehavior?.addBottomSheetCallback(bottomSheetCallBack)
    }


    fun adjustMapForBottomSheet(slideOffset: Float) {
        bounds = Helper.calculateBounds(RouteCreationHelper.latLngBounds) ?: return
        val totalSheetHeight = bottomSheet?.height
        val mapPaddingBottom = (slideOffset * totalSheetHeight!!).toInt()
        sharedViewModel.setRideOptionsSheetOffsetAndBounds(mapPaddingBottom, bounds!!)
    }

    private val bottomSheetCallBack = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (slideOffset == 0.0f || slideOffset == 1f) {
                adjustMapForBottomSheet(slideOffset)
            }
        }
    }

}