package com.example.uber.presentation.riderpresentation.bottomSheet

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils
import com.example.uber.R
import com.example.uber.core.common.Resource
import com.example.uber.core.utils.Helper
import com.example.uber.databinding.BottomSheetWhereToBinding
import com.example.uber.databinding.RideOptionsBottomSheetBinding
import com.example.uber.domain.remote.general.model.response.NearbyVehicles
import com.example.uber.presentation.riderpresentation.bottomSheet.viewadapter.CarListAdapter
import com.example.uber.presentation.riderpresentation.map.RouteCreationHelper
import com.example.uber.presentation.riderpresentation.map.utils.ShowNearbyVehicleService
import com.example.uber.presentation.riderpresentation.viewModels.LocationViewModel
import com.example.uber.presentation.riderpresentation.viewModels.RiderViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class RideOptionsBottomSheet() : Fragment(R.layout.ride_options_bottom_sheet){
    private lateinit var binding : RideOptionsBottomSheetBinding

//    private val view: WeakReference<View>,
//    private val context: Context,
//    private val viewModelStoreOwner: ViewModelStoreOwner,
//    private val viewLifecycleOwner: LifecycleOwner,
//    private val nearbyVehicleService: WeakReference<ShowNearbyVehicleService>
//    private val bottomSheet: View = view.get()!!.findViewById(R.id.ride_options_bottom_sheet)
//    private val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
//    private val shimmer: ShimmerFrameLayout by lazy {
//        view.get()!!.findViewById(R.id.shimmerLayout)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackPressed()
        val bottomSheet = requireActivity().findViewById<LinearLayout>(R.id.bottomSheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheet.layoutParams.height =
            (requireContext().resources.displayMetrics.heightPixels * 0.95).toInt()
        bottomSheetBehavior.peekHeight =
            (requireContext().resources.displayMetrics.heightPixels * 0.32).toInt()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isHideable = false
    }

    private fun handleBackPressed(){


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
        return binding.root
    }
//    private val vehicleRecyclerView: RecyclerView by lazy { bottomSheet.findViewById(R.id.vehicleRecyclerView) }
//    private lateinit var googleMap: WeakReference<GoogleMap>
//    private var isSheetExpanded: Boolean = false
//    private var vehicleDetailSheet : VehicleDetailsBottomSheet ? = null
//
//    init {
//        setBottomSheetStyle()
//        initialBottomSheetHidden()
//        setupBottomSheetCallback()
//        initializeVehicleDetailsSheet()
//        Handler(Looper.getMainLooper()).post {
//            observeNearbyVehiclesList()
//        }
//
//    }
//
//    val vehicleSheet get() = vehicleDetailSheet
//
//    fun initializeGoogleMap(googleMap: WeakReference<GoogleMap>) {
//        this.googleMap = googleMap
//        vehicleSheet?.initializeGoogleMap(googleMap)
//    }
//
//    private val riderViewModel: RiderViewModel by lazy {
//        ViewModelProvider(viewModelStoreOwner)[RiderViewModel::class.java]
//    }
//
//    private val locationViewModel: LocationViewModel by lazy {
//        ViewModelProvider(viewModelStoreOwner)[LocationViewModel::class.java]
//    }
//
//    private fun setupBottomSheetCallback() {
//        bottomSheetBehavior.addBottomSheetCallback(object :
//            BottomSheetBehavior.BottomSheetCallback() {
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                if (slideOffset == 0.0f || slideOffset == 1f){
//                    adjustMapForBottomSheet(slideOffset)
//                }
//            }
//        })
//    }
//
//
//    private fun handleStateChange(newState: Int) {
//        when {
//            newState == BottomSheetBehavior.STATE_EXPANDED && !isSheetExpanded -> {
//                expandToFillScreen()
//                isSheetExpanded = true
//            }
//
//            newState == BottomSheetBehavior.STATE_COLLAPSED && isSheetExpanded -> {
//                shrinkToFillScreen()
//                isSheetExpanded = false
//            }
//        }
//    }
//
//    fun adjustMapForBottomSheet(slideOffset: Float) {
//
//        bounds = Helper.calculateBounds(RouteCreationHelper.latLngBounds) ?: return
//        val totalSheetHeight = bottomSheet.height
//        val mapPaddingBottom = (slideOffset * totalSheetHeight).toInt()
//
//        googleMap.get()?.setPadding(0, 0, 0, mapPaddingBottom)
//        googleMap.get()?.animateCamera(
//            CameraUpdateFactory.newLatLngBounds(
//                bounds!!,
//                100
//            )
//        )
//    }
//
//
//    private var bounds: LatLngBounds? = null
//
//
//    private fun shrinkToFillScreen() {
//        bounds = Helper.calculateBounds(RouteCreationHelper.latLngBounds) ?: return
//        val bottomPadding = ConvertUtils.dp2px(0f)
//
//        googleMap.get()?.setPadding(0, 0, 0, bottomPadding)
//
//        val cameraPosition = CameraPosition.Builder()
//            .target(bounds!!.center)
//            .zoom(googleMap.get()?.cameraPosition?.zoom?.plus(2f) ?: 20f)
//            .build()
//
//        googleMap.get()?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
//
//    }
//
//
//
//    private fun expandToFillScreen() {
//        bounds = Helper.calculateBounds(RouteCreationHelper.latLngBounds) ?: return
//        val bottomPadding = ConvertUtils.dp2px(400f)
//
//        googleMap.get()?.setPadding(0, 0, 0, bottomPadding)
//        val cameraPosition = CameraPosition.Builder()
//            .target(bounds!!.center)
//            .zoom(googleMap.get()?.cameraPosition?.zoom?.minus(2f) ?: 12f)
//            .build()
//
//        googleMap.get()?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
//    }
//
//    private fun setBottomSheetStyle() {
//        bottomSheet.layoutParams.height =
//            (context.resources.displayMetrics.heightPixels * 0.70).toInt()
//        bottomSheetBehavior.peekHeight =
//            (context.resources.displayMetrics.heightPixels * 0.32).toInt()
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//        bottomSheetBehavior.isHideable = false
//    }
//
//    fun showBottomSheet() {
//        getNearbyVehicles()
//        bottomSheetBehavior.isHideable = false
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//        shimmer.startShimmer()
//    }
//
//    private fun initializeVehicleDetailsSheet(){
//        vehicleDetailSheet = VehicleDetailsBottomSheet(view,context)
//    }
//
//
//
//    private fun initialBottomSheetHidden() {
//        bottomSheetBehavior.isHideable = true
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//    }
//
//    fun hideBottomSheet() {
//        bottomSheetBehavior.isHideable = true
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//    }
//
//    fun bottomSheetBehaviour(): Int {
//        return bottomSheetBehavior.state
//    }
//
//    private fun getNearbyVehicles() {
//        riderViewModel.getNearbyVehicles(
//            riderViewModel.riderId!!,
//            locationViewModel.pickUpLocation!!
//        )
//    }
//
//    private fun createRecyclerViewAdapter(nearbyVehicles: List<NearbyVehicles>) {
//        val adapter = CarListAdapter(nearbyVehicles)
//        adapter.onItemClicked = {it,isSelectedAgain->
//            nearbyVehicleService.get()?.onCarItemListClickListener(it)
//            if(isSelectedAgain){
//                vehicleDetailSheet?.showSheet(it)
//                hideBottomSheet()
//            }
//        }
//
//        vehicleRecyclerView.layoutManager = LinearLayoutManager(context)
//        vehicleRecyclerView.adapter = adapter
//        hideShimmer()
//    }
//
//    private fun hideShimmer() {
//        shimmer.visibility = View.GONE
//    }
//
//    private fun observeNearbyVehiclesList() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            with(riderViewModel) {
//                nearbyVehicles.collectLatest {
//                    when (it) {
//                        is Resource.Success -> {
//                            if (it.data != null) {
//                                withContext(Dispatchers.Main) {
//                                    createRecyclerViewAdapter(it.data)
//                                }
//                            }
//                        }
//
//                        else -> Unit
//                    }
//                }
//            }
//        }
//    }

}