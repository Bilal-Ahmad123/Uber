package com.example.uber.presentation.map


import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.uber.BuildConfig
import com.example.uber.R
import com.example.uber.core.RxBus.RxBus
import com.example.uber.core.RxBus.RxEvent
import com.example.uber.core.interfaces.IActions
import com.example.uber.core.interfaces.utils.mode.CheckMode
import com.example.uber.core.utils.FetchLocation
import com.example.uber.core.utils.permissions.PermissionManagers
import com.example.uber.core.utils.system.SystemInfo
import com.example.uber.databinding.FragmentPickUpMapBinding
import com.example.uber.presentation.bottomSheet.BottomSheetManager
import com.example.uber.presentation.bottomSheet.RideOptionsBottomSheet
import com.example.uber.presentation.viewModels.MapboxViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.android.gestures.MoveGestureDetector
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
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import kotlin.math.abs

@AndroidEntryPoint
class PickUpMapFragment : Fragment(), OnMapReadyCallback, IActions {
    private var _map: MapboxMap? = null
    private val map get() = _map!!
    private var _binding: FragmentPickUpMapBinding? = null
    private val binding get() = _binding!!
    private var loadedMapStyle: Style? = null
    private var bottomSheetManager: BottomSheetManager? = null
    private var _rideOptionsBottomSheet: RideOptionsBottomSheet? = null
    private var isPopulatingLocation = false
    private lateinit var bottomSheetView: LinearLayout
    private lateinit var pickupTextView: EditText
    private lateinit var dropOffTextView: EditText
    private val mapboxViewModel: MapboxViewModel by viewModels()
    private var isPickupEtInFocus = false
    private var isDropOffEtInFocus = false
    private var routeHelper: RouteCreationHelper? = null
    private var _areListenersRegistered = false
    private val _compositeDisposable = CompositeDisposable()


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
        initializeBottomSheets(view)
        initializeBottomSheetViews()
        setBackButtonOnClickListener()
        editTextFocusChangeListener()
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        if (isAdded) {
            setUpCurrentLocationButton()
            requestLocationPermission()
            getInitialPickUpLocation()
        }

    }

    private fun initializeBottomSheets(view: View) {
        bottomSheetManager =
            BottomSheetManager(
                view,
                requireContext(),
                this,
                viewLifecycleOwner,
                mapboxViewModel,
            )
        _rideOptionsBottomSheet = RideOptionsBottomSheet(view, requireContext())


    }

    private fun initializeBottomSheetViews() {
        bottomSheetView = binding.root.findViewById<LinearLayout>(R.id.bottom_sheet)
        pickupTextView = binding.root.findViewById<EditText>(R.id.ti_pickup)
        dropOffTextView = binding.root.findViewById<EditText>(R.id.ti_drop_off)
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
        onAddCameraAndMoveListeners()
        RouteCreationHelper.initInstances(
            WeakReference(binding.mapView),
            WeakReference(mapboxMap),
            WeakReference(requireContext()),
            WeakReference(_rideOptionsBottomSheet),
            WeakReference(bottomSheetManager),
            WeakReference(this),
            mapboxViewModel
        )
    }

    private fun animateCameraToCurrentLocation(lastKnownLocation: Location?) {
        if (map != null) {
            val userLatLng = lastKnownLocation?.let { LatLng(it.latitude, it.longitude) }
            userLatLng?.let { CameraUpdateFactory.newLatLngZoom(it, 13.0) }
                ?.let { map.moveCamera(it) }
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
        onRemoveCameraAndMoveListener()
        _map = null
        bottomSheetManager = null
        FetchLocation.cleanResources()
        routeHelper?.clearResources()
        routeHelper = null
        _rideOptionsBottomSheet = null
        _compositeDisposable.dispose()
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
        PermissionManagers.requestPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) {
            if (it) {
                onGranted.invoke()
            }
        }
    }

    private fun setBackButtonOnClickListener() {
        binding.backFbn.setOnClickListener {
            if (bottomSheetManager?.bottomSheetBehaviour() == BottomSheetBehavior.STATE_COLLAPSED || bottomSheetManager?.bottomSheetBehaviour() == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetManager?.showBottomSheet()
            }
            _rideOptionsBottomSheet?.hideBottomSheet()
            if (RouteCreationHelper.doesLineManagerExist()) {
                deleteRoutes()
            }
            if (!_areListenersRegistered) {
                onAddCameraAndMoveListeners()
            }
            requestEditTextDropOffFocus()

        }
    }

    private fun requestEditTextDropOffFocus(){
        bottomSheetManager?.requestEditTextDropOffFocus()
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
                        mapboxViewModel.setPickUpLocationName(
                            map.cameraPosition.target.latitude,
                            map.cameraPosition.target.longitude
                        )
                    } else if (isDropOffEtInFocus) {
                        mapboxViewModel.setDropOffLocationName(
                            map.cameraPosition.target.latitude,
                            map.cameraPosition.target.longitude
                        )
                    }
                } finally {
                    isPopulatingLocation = false
                }
            }.onFailure {
                Log.e("Location Error", it.message.toString())
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun editTextFocusChangeListener() {

        _compositeDisposable.add(
            RxBus.listen(RxEvent.EventEditTextFocus::class.java).subscribe {
                if (it.isPickUpEditTextFocus) {
                    animateToPickUpLocation()
                } else if (it.isDropOffEditTextFocus) {
                    animateToDropOffLocation()
                }
                isPickupEtInFocus = it.isPickUpEditTextFocus
                isDropOffEtInFocus = it.isDropOffEditTextFocus
            }
        )
    }

    private fun getInitialPickUpLocation() {
        checkLocationPermission(null) {
            FetchLocation.getCurrentLocation(
                this@PickUpMapFragment,
                requireContext()
            ) { location ->
                runCatching {
                    mapboxViewModel.setPickUpLocationName(
                        location!!.latitude, location.longitude
                    )
                }
            }

        }


    }

    private fun animateToPickUpLocation() {
        val locationMapper = FetchLocation.customLocationMapper(
            mapboxViewModel.pickUpLatitude, mapboxViewModel.pickUpLongitude
        )
        runCatching { animateCameraToCurrentLocation(locationMapper) }

    }

    private fun animateToDropOffLocation() {
        val locationMapper = FetchLocation.customLocationMapper(
            mapboxViewModel.dropOffLatitude,
            mapboxViewModel.dropOffLongitude
        )


        runCatching { animateCameraToCurrentLocation(locationMapper) }

    }

    private fun createRoute(
        pickUpLatLng: LatLng? = null,
        dropOffLatLng: LatLng? = null
    ) {
        val pickUp = pickUpLatLng ?: LatLng(
            mapboxViewModel.pickUpLatitude,
            mapboxViewModel.pickUpLongitude,
        )
        val dropOff = dropOffLatLng ?: LatLng(
            mapboxViewModel.dropOffLatitude,
            mapboxViewModel.dropOffLongitude
        )

        hideLocationPickerMarker()
        onRemoveCameraAndMoveListener()
        lifecycleScope.launch(Dispatchers.IO) {
            RouteCreationHelper.createRoute(
                Point.fromLngLat(
                    pickUp.longitude,
                    pickUp.latitude
                ),
                Point.fromLngLat(
                    dropOff.longitude,
                    dropOff.latitude
                )
            )
        }
        showRideOptionsBottomSheet()
    }

    private fun hideLocationPickerMarker() {
        binding.activityMainCenterLocationPin.visibility = View.GONE
    }


    private fun showRideOptionsBottomSheet() {
        _rideOptionsBottomSheet?.showBottomSheet()
    }

    private fun deleteRoutes() {
        RouteCreationHelper.deleteRoute()
        showLocationPickerMarker()
    }

    fun showLocationPickerMarker() {
        binding.activityMainCenterLocationPin.visibility = View.VISIBLE
    }


    fun onAddCameraAndMoveListeners() {
        if (!_areListenersRegistered) {
            _areListenersRegistered = true
            _map?.addOnMoveListener(moveListener)
            _map?.addOnCameraIdleListener(cameraPositionChangeListener)
        }
    }

    fun onRemoveCameraAndMoveListener() {
        if (_areListenersRegistered) {
            _areListenersRegistered = false
            _map?.removeOnMoveListener(moveListener)
            _map?.removeOnCameraIdleListener(cameraPositionChangeListener)
        }
    }

    override fun createRouteAction(
        pickUpLatLng: LatLng?,
        dropOffLatLng: LatLng?
    ) {
        createRoute(pickUpLatLng, dropOffLatLng)
    }


}






