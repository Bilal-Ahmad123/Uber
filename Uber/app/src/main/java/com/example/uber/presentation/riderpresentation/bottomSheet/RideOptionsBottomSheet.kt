package com.example.uber.presentation.riderpresentation.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uber.R
import com.example.uber.core.common.Resource
import com.example.uber.core.enums.SheetState
import com.example.uber.core.utils.Helper
import com.example.uber.databinding.RideOptionsBottomSheetBinding
import com.example.uber.domain.remote.general.model.response.NearbyVehicles
import com.example.uber.presentation.riderpresentation.bottomSheet.viewadapter.CarListAdapter
import com.example.uber.presentation.riderpresentation.map.RouteCreationHelper
import com.example.uber.presentation.riderpresentation.viewModels.LocationViewModel
import com.example.uber.presentation.riderpresentation.viewModels.MapAndSheetsSharedViewModel
import com.example.uber.presentation.riderpresentation.viewModels.RiderViewModel
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RideOptionsBottomSheet() : Fragment(R.layout.ride_options_bottom_sheet) {
    private var binding: RideOptionsBottomSheetBinding? = null
    private val sharedViewModel: MapAndSheetsSharedViewModel by activityViewModels()
    private val riderViewModel: RiderViewModel by activityViewModels<RiderViewModel>()
    private var bottomSheet: LinearLayout? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private val locationViewModel: LocationViewModel by activityViewModels<LocationViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackPressed()
        setBottomSheetStyle()
        setupBottomSheetCallback()
        observeNearbyVehiclesList()
        getNearbyVehicles()
        sharedViewModel.setCurrentOpenedSheet(SheetState.RIDE_SHEET)
    }

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity()
                .findNavController(R.id.nav_host_bottom_sheet)
                .popBackStack()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RideOptionsBottomSheetBinding.inflate(inflater, container, false)
        return binding?.root
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

    override fun onDestroyView() {
        super.onDestroyView()
        bottomSheetBehavior?.removeBottomSheetCallback(bottomSheetCallBack)
        binding = null
        bottomSheet = null
        bottomSheetBehavior = null
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

    private fun getNearbyVehicles() {
        riderViewModel.getNearbyVehicles(
            riderViewModel.riderId!!,
            locationViewModel.pickUpLocation!!
        )
    }


    private fun createRecyclerViewAdapter(nearbyVehicles: List<NearbyVehicles>) {
        val adapter = CarListAdapter(nearbyVehicles)
        adapter.onItemClicked = { it, isSelectedAgain ->
            if (isSelectedAgain) {
                findNavController().navigate(R.id.vehicleDetailsBottomSheet)
                sharedViewModel.setSelectedVehicle(it)
                sharedViewModel.vehicleSelected(it)
            }
        }

        binding?.vehicleRecyclerView?.layoutManager = LinearLayoutManager(context)
        binding?.vehicleRecyclerView?.adapter = adapter
        hideShimmer()
    }

    private fun hideShimmer() {
        binding?.shimmerLayout?.visibility = View.GONE
    }

    private fun observeNearbyVehiclesList() {
        viewLifecycleOwner.lifecycleScope.launch {
            with(riderViewModel) {
                nearbyVehicles.collectLatest {
                    when (it) {
                        is Resource.Success -> {
                            if (it.data != null) {
                                withContext(Dispatchers.Main) {
                                    createRecyclerViewAdapter(it.data)
                                }
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

}