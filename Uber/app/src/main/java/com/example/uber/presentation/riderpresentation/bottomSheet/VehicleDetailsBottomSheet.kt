package com.example.uber.presentation.riderpresentation.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.uber.R
import com.example.uber.core.enums.SheetState
import com.example.uber.core.utils.Helper
import com.example.uber.core.utils.StringHelper
import com.example.uber.databinding.VehicleDetailsBottomSheetBinding
import com.example.uber.domain.remote.general.model.response.NearbyVehicles
import com.example.uber.presentation.riderpresentation.map.RouteCreationHelper
import com.example.uber.presentation.riderpresentation.viewModels.MapAndSheetsSharedViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior


class VehicleDetailsBottomSheet() : Fragment(R.layout.vehicle_details_bottom_sheet) {

    private var binding: VehicleDetailsBottomSheetBinding? = null
    private val sharedViewModel : MapAndSheetsSharedViewModel by activityViewModels<MapAndSheetsSharedViewModel>()
    private var bottomSheet : LinearLayout ? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomSheetStyle()
        initializeVehicleDetails(sharedViewModel.vehicleSelected!!)
        handleBackPressed()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = VehicleDetailsBottomSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity()
                .findNavController(R.id.nav_host_bottom_sheet)
                .popBackStack()
        }
    }


    private fun setBottomSheetStyle() {
        sharedViewModel.setCurrentOpenedSheet(SheetState.VEHICLE_SHEET)
        bottomSheet = requireActivity().findViewById<LinearLayout>(R.id.bottomSheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
        bottomSheet?.layoutParams?.height =
            (requireContext().resources.displayMetrics.heightPixels * 0.55).toInt()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.isDraggable = false
        adjustMapForBottomSheet(1f)
    }

    private fun initializeVehicleDetails(vehicle: NearbyVehicles) {
        binding?.apply {
            Glide.with(requireContext()).load(vehicle.image)
                .into(vehicleImage)
            vehicleName.text = vehicle.name
            maxSeats.text = vehicle.seats.toString()
            timeOfArrival.text = StringHelper.calculateTimeWithVehicleTime(vehicle.time)
            minsAway.text = StringHelper.showTimeAway(vehicle.time)
            fare.text = StringHelper.showCurrency(vehicle.fare)
            vehicleDescription.text = vehicle.description
            chooseVehicle.text = "Choose " + vehicle.name
        }
    }

    fun adjustMapForBottomSheet(slideOffset: Float) {
        val bounds = Helper.calculateBounds(RouteCreationHelper.latLngBounds) ?: return
        bottomSheet?.doOnLayout {
            val totalSheetHeight = bottomSheet?.height
            val mapPaddingBottom = (slideOffset * totalSheetHeight!!).toInt()
            sharedViewModel.setRideOptionsSheetOffsetAndBounds(mapPaddingBottom, bounds)
        }
    }

}