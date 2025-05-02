package com.example.uber.presentation.riderpresentation.bottomSheet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.uber.R
import com.example.uber.core.utils.Helper
import com.example.uber.databinding.FragmentRideRequestedSheetBinding
import com.example.uber.presentation.riderpresentation.map.Routes.RouteCreationHelper
import com.example.uber.presentation.riderpresentation.viewModels.MapAndSheetsSharedViewModel
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.bottomsheet.BottomSheetBehavior

class RideRequestedSheet : Fragment(R.layout.fragment_ride_requested_sheet) {
    private val sharedViewModel: MapAndSheetsSharedViewModel by activityViewModels<MapAndSheetsSharedViewModel>()
    private var bottomSheet: LinearLayout? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var binding:FragmentRideRequestedSheetBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRideRequestedSheetBinding.inflate(inflater,container,false)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomSheetStyle()
        handleBackPressed()

    }

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity()
                .findNavController(R.id.nav_host_bottom_sheet)
                .popBackStack()
        }
    }
    private var bounds: LatLngBounds? = null
    private fun setBottomSheetStyle() {
        bottomSheet = requireActivity().findViewById<LinearLayout>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)

        bottomSheet?.layoutParams?.height =
            (requireContext().resources.displayMetrics.heightPixels * 0.32).toInt()
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior?.isHideable = false
        bottomSheetBehavior?.isDraggable = false
        adjustMapForBottomSheet(Helper.calculateSheetOffSet((bottomSheet!!.parent as View).height,bottomSheetBehavior!!.peekHeight,bottomSheet!!.top))
    }



    private fun adjustMapForBottomSheet(slideOffset: Float) {
        bounds = Helper.calculateBounds(RouteCreationHelper.latLngBounds) ?: return
        val totalSheetHeight = bottomSheet?.height
        val mapPaddingBottom = (slideOffset * totalSheetHeight!!).toInt()
        sharedViewModel.setRideOptionsSheetOffsetAndBounds(mapPaddingBottom, bounds!!)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        bounds = null
        bottomSheet = null
        bottomSheetBehavior = null
    }

}