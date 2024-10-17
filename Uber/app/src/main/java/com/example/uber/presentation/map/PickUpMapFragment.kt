package com.example.uber.presentation.map


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
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
import com.example.uber.core.utils.system.SystemInfo
import com.example.uber.databinding.FragmentPickUpMapBinding
import com.example.uber.presentation.bottomSheet.BottomSheetManager
import com.example.uber.presentation.viewModels.DropOffLocationViewModel
import com.example.uber.presentation.viewModels.PickUpLocationViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.MapboxMap.OnCameraIdleListener
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.LineManager
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs


class PickUpMapFragment : Fragment(), OnMapReadyCallback, IBottomSheetListener {
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
    private var routeHelper: RouteCreationHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(
            requireContext(),
            BuildConfig.MAPBOX_TOKEN
        );
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
                createRoute()
            }
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        if (isAdded) {
            setUpCurrentLocationButton()
            requestLocationPermission()
            getInitialPickUpLocation()
        }

    }

    private fun showUserLocation(loadedMapStyle: Style) {
        val locationComponentActivationOptions =
            LocationComponentActivationOptions.builder(requireContext(), loadedMapStyle)
                .useDefaultLocationEngine(true)
                .build()
        checkLocationPermission(null) {
            FetchLocation.getCurrentLocation(this@PickUpMapFragment, requireContext()) { location ->
                animateCameraToCurrentLocation(location)
            }
        }
        showUserCurrentLocation(locationComponentActivationOptions)


    }

    private fun requestLocationPermission() {
        checkLocationPermission("Need Access to Location") {
            binding.mapView.getMapAsync { mapboxMap ->
                this@PickUpMapFragment._map = mapboxMap
                mapboxMap.setStyle(getCurrentMapStyle()) {
                    loadedMapStyle = it
                    showUserLocation(it)
                }
            }

        }

    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        _map = mapboxMap
        mapboxMap.setStyle(getCurrentMapStyle())
        mapboxMap.addOnMoveListener(moveListener)
        mapboxMap.addOnCameraIdleListener(cameraPositionChangeListener)
        routeHelper = RouteCreationHelper(binding.mapView, mapboxMap)
    }

    private fun animateCameraToCurrentLocation(lastKnownLocation: Location?) {
        if (map != null) {
            val userLatLng = lastKnownLocation?.let { LatLng(it.latitude, it.longitude) }
            userLatLng?.let { CameraUpdateFactory.newLatLngZoom(it, 13.0) }
                ?.let { map.animateCamera(it) }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
        cleanUpResources()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    private fun setUpCurrentLocationButton() {
        binding.currLocationBtn.setOnClickListener {
            showUserLocation(loadedMapStyle!!)
            hideUserLocationButton()
        }
    }

    private fun cleanUpResources() {
        _binding = null
        loadedMapStyle = null
        map.removeOnMoveListener(moveListener)
        map.removeOnCameraIdleListener(cameraPositionChangeListener)
        _map = null
        bottomSheetManager = null
        FetchLocation.cleanResources()
        routeHelper = null


    }

    private val moveListener: MapboxMap.OnMoveListener = object : MapboxMap.OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            if (binding.currLocationBtn.visibility != View.VISIBLE) {
                fadeInUserLocationButton()
            }
        }

        override fun onMove(detector: MoveGestureDetector) {


        }

        override fun onMoveEnd(detector: MoveGestureDetector) {

        }

    }

    private val cameraPositionChangeListener = OnCameraIdleListener {
        if (!isPopulatingLocation) {
            fetchLocation()
        }
    }

    private fun hideUserLocationButton() {
        floatingButtonFadeOutAnimation()
    }


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

    private fun getCurrentMapStyle(): String =
        if (CheckMode.isDarkMode(requireContext())) Style.DARK else Style.LIGHT

    private fun showUserCurrentLocation(locationComponentActivationOptions: LocationComponentActivationOptions) {
        checkLocationPermission(null) {
            map.locationComponent.apply {
                activateLocationComponent(locationComponentActivationOptions)
                isLocationComponentEnabled = true
                cameraMode = CameraMode.TRACKING
                renderMode = RenderMode.COMPASS

            }
        }
    }

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
                        pickUpLocationViewModel.geoCodeLocation(
                            map.cameraPosition.target.latitude, map.cameraPosition.target.longitude
                        )
                    } else if (isDropOffEtInFocus) {
                        dropOffLocationViewModel.geoCodeLocation(
                            map.cameraPosition.target.latitude, map.cameraPosition.target.longitude
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

    private fun getInitialPickUpLocation() {
        checkLocationPermission(null) {
            FetchLocation.getCurrentLocation(this@PickUpMapFragment, requireContext()) { location ->
                runCatching {
                    pickUpLocationViewModel.geoCodeLocation(
                        location!!.latitude, location.longitude
                    )
                }
            }
        }


    }

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

    private fun createRoute() {
        lifecycleScope.launch(Dispatchers.IO) {
            routeHelper?.createRoute(
                Point.fromLngLat(
                    pickUpLocationViewModel.longitude,
                    pickUpLocationViewModel.latitude
                ),
                Point.fromLngLat(
                    dropOffLocationViewModel.longitude,
                    dropOffLocationViewModel.latitude
                )
            )
        }
    }
}






