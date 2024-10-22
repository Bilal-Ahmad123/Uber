package com.example.uber.presentation.map

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.uber.BuildConfig
import com.example.uber.R
import com.example.uber.core.interfaces.IBottomSheetListener
import com.example.uber.core.interfaces.utils.mode.CheckMode
import com.example.uber.core.interfaces.utils.permissions.Permission
import com.example.uber.core.interfaces.utils.permissions.PermissionManager
import com.example.uber.core.utils.FetchLocation
//import com.example.uber.core.utils.FetchLocation
import com.example.uber.core.utils.system.SystemInfo
import com.example.uber.databinding.FragmentPickUpMapBinding
import com.example.uber.presentation.bottomSheet.BottomSheetManager
import com.example.uber.presentation.viewModels.DropOffLocationViewModel
import com.example.uber.presentation.viewModels.PickUpLocationViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.pitch
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.viewport
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import kotlin.math.abs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.delegates.listeners.OnMapIdleListener
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures

class PickUpMapFragment : Fragment(), IBottomSheetListener {
    private var _map: MapboxMap? = null
    private val map get() = _map!!
    private val permissionManager = PermissionManager.from(this)
    private var _binding: FragmentPickUpMapBinding? = null
    private val binding get() = _binding!!
    private var loadedMapStyle: Style? = null
    private var bottomSheetManager: BottomSheetManager? = null
    private var isPopulatingLocation = false
    private lateinit var bottomSheetView: LinearLayout
    private lateinit var pickupTextView: EditText
    private lateinit var dropOffTextView: EditText
    private lateinit var confirmDestinationBtn: AppCompatButton
    private val pickUpLocationViewModel: PickUpLocationViewModel by activityViewModels()
    private val dropOffLocationViewModel: DropOffLocationViewModel by activityViewModels()
    private var isPickupEtInFocus = false
    private var isDropOffEtInFocus = false
    private lateinit var currentLocation: Point


    //    private var routeHelper: RouteCreationHelper? = null
    private val navigationLocationProvider = NavigationLocationProvider()
    private lateinit var mapView: MapView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapboxOptions.accessToken = BuildConfig.MAPBOX_TOKEN
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPickUpMapBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetView = binding.root.findViewById<LinearLayout>(R.id.bottom_sheet)
        pickupTextView = binding.root.findViewById<EditText>(R.id.ti_pickup)
        dropOffTextView = binding.root.findViewById<EditText>(R.id.ti_drop_off)
        confirmDestinationBtn =
            binding.root.findViewById<AppCompatButton>(R.id.btn_confirm_destination)
        setBackButtonOnClickListener()
        editTextFocusChangeListener()
        bottomSheetManager =
            BottomSheetManager(
                view,
                requireContext(),
                this,
                viewLifecycleOwner,
                pickUpLocationViewModel,
                dropOffLocationViewModel
            )
        binding.root.findViewById<AppCompatButton>(R.id.btn_confirm_destination)
            .setOnClickListener {
//                createRoute()
            }
        mapView = binding.mapView
        // Add the map view to the activity (you can also add it to other views as a child)
        mapView.mapboxMap.loadStyleUri(getCurrentMapStyle())
//        setupLocationComponent()
        initializeMapBox()
        setupMoveListener()
        cameraIdleListener



        if (isAdded) {
            setUpCurrentLocationButton()
//            requestLocationPermission()
//            getInitialPickUpLocation()
        }

    }

    private fun showUserLocation() {
        checkLocationPermission(null) {
            FetchLocation.getCurrentLocation(requireContext()) { location ->
                animateCameraToCurrentLocation(FetchLocation.customLocationMapper(
                    location!!.latitude(), location.longitude()
                ))
            }
        }
    }

    private fun requestLocationPermission() {
        checkLocationPermission("Need Access to Location") {
            _map = binding.mapView.getMapboxMap()
            _map!!.loadStyleUri(getCurrentMapStyle())
        }

    }

    fun onMapReady(mapboxMap: MapboxMap) {
        _map = mapboxMap
//        mapboxMap.setStyle(getCurrentMapStyle())
//        mapboxMap.addOnCameraIdleListener(cameraPositionChangeListener)
//        routeHelper = RouteCreationHelper(WeakReference(binding.mapView), WeakReference(mapboxMap),requireContext())
    }

    private fun animateCameraToCurrentLocation(lastKnownLocation: Location?) {
        if (lastKnownLocation != null) {
            val cameraPosition = CameraOptions.Builder()
                .center(Point.fromLngLat(lastKnownLocation.longitude, lastKnownLocation.latitude))
                .zoom(13.0)
                .build()
            mapView.camera.easeTo(cameraPosition, mapAnimationOptions {
                duration(2000)
            })
        } else {
            Log.e("Map", "Last known location is null")
        }
    }

    override fun onStart() {
        super.onStart()
//        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
//        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
//        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
//        binding.mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        binding.mapView.onDestroy()
//        cleanUpResources()
    }

    override fun onLowMemory() {
        super.onLowMemory()
//        binding.mapView.onLowMemory()
    }

    private fun setUpCurrentLocationButton() {
        binding.currLocationBtn.setOnClickListener {
            showUserLocation()
            hideUserLocationButton()
        }
    }
//
//    private fun cleanUpResources() {
//        _binding = null
//        loadedMapStyle = null
//        map.removeOnMoveListener(moveListener)
//        map.removeOnCameraIdleListener(cameraPositionChangeListener)
//        _map = null
//        bottomSheetManager = null
//        FetchLocation.cleanResources()
//        routeHelper = null
//
//
//    }

    //    private val moveListener: MapboxMap.OnMoveListener = object : MapboxMap.OnMoveListener {
//        override fun onMoveBegin(detector: MoveGestureDetector) {
//            if (binding.currLocationBtn.visibility != View.VISIBLE) {
//                fadeInUserLocationButton()
//            }
//        }
//
//        override fun onMove(detector: MoveGestureDetector) {
//
//
//        }
//
//        override fun onMoveEnd(detector: MoveGestureDetector) {
//
//        }
//
//    }
//
//    private val cameraPositionChangeListener = OnCameraIdleListener {
//        if (!isPopulatingLocation) {
//            fetchLocation()
//        }
//    }
//
    private fun hideUserLocationButton() {
        floatingButtonFadeOutAnimation()
    }
//
//

    private fun floatingButtonFadeOutAnimation() {
        binding.currLocationBtn.animate()
            .alpha(0f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.currLocationBtn.visibility = View.GONE
                }
            })
    }
//
    private fun fadeInUserLocationButton() {
        binding.currLocationBtn.apply {
            visibility = View.VISIBLE
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(300)
                .setListener(null)
        }
    }
//
    private fun getCurrentMapStyle(): String =
        if (CheckMode.isDarkMode(requireContext())) Style.DARK else Style.LIGHT


    private fun checkLocationPermission(rationale: String?, onGranted: () -> Unit) {
        val request = permissionManager.request(Permission.Location)
        if (rationale != null) {
            request.rationale(rationale)
        }
        request.checkPermission { granted ->
            if (granted) {
                onGranted.invoke()
            }

        }
    }

    private fun setBackButtonOnClickListener() {
        binding.backFbn.setOnClickListener {
            if (bottomSheetManager?.bottomSheetBehavior?.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetManager?.bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun onBottomSheetSlide(slideOffset: Float) {
        binding.backFbn.apply {
            if (slideOffset == 1.0f) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
                alpha = abs(slideOffset - 1.0f)
            }
        }
    }

    override fun onBottomSheetStateChanged(newState: Int) {
    }


    private fun fetchLocation() {
        if (SystemInfo.CheckInternetConnection(requireContext())) {
            runCatching {
                try {
                    if (isPickupEtInFocus) {
                        pickUpLocationViewModel.setPickUpLocationName(
                            map.cameraState.center.latitude(),
                            map.cameraState.center.longitude()
                        )
                    } else if (isDropOffEtInFocus) {
                        dropOffLocationViewModel.setPickUpLocationName(
                            map.cameraState.center.latitude(),
                            map.cameraState.center.longitude()
                        )
                    }
                } finally {
                    isPopulatingLocation = false
                }
            }
        }
    }

    private fun editTextFocusChangeListener() {
        pickupTextView.setOnFocusChangeListener { view, b ->
            if (b) {
                isPickupEtInFocus = true
                isDropOffEtInFocus = false
                animateToPickUpLocation()
            }
        }
        dropOffTextView.setOnFocusChangeListener { view, b ->
            if (b) {
                isPickupEtInFocus = false
                isDropOffEtInFocus = true
                animateToDropOffLocation()
            }
        }
    }

    //
//    private fun getInitialPickUpLocation() {
//        checkLocationPermission(null) {
//            FetchLocation.getCurrentLocation(
//                this@PickUpMapFragment,
//                requireContext()
//            ) { location ->
//                runCatching {
//                    pickUpLocationViewModel.setPickUpLocationName(
//                        location!!.latitude, location.longitude
//                    )
//                }
//            }
//
//        }
//
//
//    }
//
    private fun animateToPickUpLocation() {
        val locationMapper = FetchLocation.customLocationMapper(
            pickUpLocationViewModel.latitude, pickUpLocationViewModel.longitude
        )
        runCatching { animateCameraToCurrentLocation(locationMapper) }

    }

    private fun animateToDropOffLocation() {
        val locationMapper = FetchLocation.customLocationMapper(
            dropOffLocationViewModel.latitude,
            dropOffLocationViewModel.longitude
        )
        runCatching { animateCameraToCurrentLocation(locationMapper) }

    }
//
//    private fun createRoute() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            routeHelper?.createRoute(
//                Point.fromLngLat(
//                    pickUpLocationViewModel.longitude,
//                    pickUpLocationViewModel.latitude
//                ),
//                Point.fromLngLat(
//                    dropOffLocationViewModel.longitude,
//                    dropOffLocationViewModel.latitude
//                )
//            )
//        }
//    }


    private fun initializeMapBox() {
        with(mapView) {
            location.enabled = true
            location.puckBearing = PuckBearing.COURSE
            location.puckBearingEnabled = true
        }
        getCurrentLocation()

    }

    private fun getCurrentLocation() {
        checkLocationPermission(null) {
            FetchLocation.getCurrentLocation(requireContext()) { point ->
                mapView.mapboxMap
                    .apply {
                        setCamera(
                            CameraOptions.Builder()
                                .center(point)
                                .zoom(13.0)
                                .build()
                        )
                    }
            }
        }
    }

    private fun setupMoveListener() {
        val gesturesPlugin = mapView.gestures

        gesturesPlugin.addOnMoveListener(object : OnMoveListener {
            override fun onMoveBegin(detector: MoveGestureDetector) {
                Log.d("onMoveBegin", "onMoveBegin")
                // Similar to SDK 9: Handle the start of a move gesture
                if (binding.currLocationBtn.visibility != View.VISIBLE) {
                    fadeInUserLocationButton()  // Show user location button
                }
            }

            override fun onMove(detector: MoveGestureDetector): Boolean {
                // Handle ongoing move gestures (if needed)
                return false
            }

            override fun onMoveEnd(detector: MoveGestureDetector) {
                // Handle the end of the move gesture (if needed)
            }
        })
    }

    private val  cameraIdleListener= OnMapIdleListener{
        if (!isPopulatingLocation) {
            fetchLocation() 
            hideUserLocationButton()
        }
    }


}






