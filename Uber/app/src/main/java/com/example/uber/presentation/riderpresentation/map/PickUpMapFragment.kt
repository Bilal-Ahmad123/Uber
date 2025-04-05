package com.example.uber.presentation.riderpresentation.map


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
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.uber.R
import com.example.uber.core.RxBus.RxBus
import com.example.uber.core.RxBus.RxEvent
import com.example.uber.core.interfaces.IActions
import com.example.uber.core.interfaces.utils.mode.CheckMode
import com.example.uber.core.utils.FetchLocation
import com.example.uber.core.utils.permissions.PermissionManagers
import com.example.uber.core.utils.system.SystemInfo
import com.example.uber.databinding.FragmentPickUpMapBinding
import com.example.uber.domain.local.location.model.DriverLocationMarker
import com.example.uber.presentation.riderpresentation.bottomSheet.BottomSheetManager
import com.example.uber.presentation.riderpresentation.bottomSheet.RideOptionsBottomSheet
import com.example.uber.presentation.riderpresentation.map.utils.ShowNearbyVehicleService
import com.example.uber.presentation.riderpresentation.viewModels.GoogleViewModel
import com.example.uber.presentation.riderpresentation.viewModels.LocationViewModel
import com.example.uber.presentation.riderpresentation.viewModels.MapboxViewModel
import com.example.uber.presentation.riderpresentation.viewModels.RiderViewModel
import com.example.uber.presentation.riderpresentation.viewModels.SocketViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.UUID
import kotlin.math.abs


@AndroidEntryPoint
class PickUpMapFragment : Fragment(), IActions, OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback {
    private var _binding: FragmentPickUpMapBinding? = null
    private val binding get() = _binding!!
    private var bottomSheetManager: BottomSheetManager? = null
    private var _rideOptionsBottomSheet: RideOptionsBottomSheet? = null
    private var isPopulatingLocation = false
    private lateinit var bottomSheetView: LinearLayout
    private lateinit var pickupTextView: EditText
    private lateinit var dropOffTextView: EditText
    private val mapboxViewModel: MapboxViewModel by viewModels()
    private val googleViewModel: GoogleViewModel by viewModels()
    private val socketViewModel: SocketViewModel by activityViewModels()
    private var isPickupEtInFocus = false
    private var isDropOffEtInFocus = true
    private var routeHelper: RouteCreationHelper? = null
    private var _areListenersRegistered = false
    private val _compositeDisposable = CompositeDisposable()
    private lateinit var mapFrag: SupportMapFragment
    private lateinit var googleMap: GoogleMap
    private var currentLocation: Location? = null
    private val drivers = mutableMapOf<UUID, DriverLocationMarker>()
    private val locationViewModel: LocationViewModel by activityViewModels<LocationViewModel>()
    private val riderViewModel: RiderViewModel by activityViewModels<RiderViewModel>()
    private var nearbyVehicles: ShowNearbyVehicleService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        setUpGoogleMap()
        if (isAdded) {
            setUpCurrentLocationButton()
            requestLocationPermission()
            getInitialPickUpLocation()
        }
        handleOnBackPressed()
    }

    private fun initializeNearbyVehicles() {
        nearbyVehicles = ShowNearbyVehicleService(requireActivity(), viewLifecycleOwner,
            WeakReference(requireContext())
        )
    }

    private fun handleOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback {
            when (_rideOptionsBottomSheet?.bottomSheetBehaviour()) {
                BottomSheetBehavior.STATE_EXPANDED -> {
                    _rideOptionsBottomSheet?.hideBottomSheet()
                    bottomSheetManager?.showBottomSheet()
                    routeHelper?.deleteEveryThingOnMap()
                    showLocationPickerMarker()
                    onAddCameraAndMoveListeners()
                }

                BottomSheetBehavior.STATE_COLLAPSED -> {
                    _rideOptionsBottomSheet?.hideBottomSheet()
                    bottomSheetManager?.showBottomSheet()
                    routeHelper?.deleteEveryThingOnMap()
                    showLocationPickerMarker()
                    onAddCameraAndMoveListeners()
                }

                else -> {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    private fun setUpGoogleMap() {
        mapFrag =
            childFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment
        mapFrag.getMapAsync(this)
    }


    private fun initializeBottomSheets(view: View) {
        initializeNearbyVehicles()
        bottomSheetManager =
            BottomSheetManager.initialize(
                view,
                WeakReference(requireContext()),
                WeakReference(this),
                viewLifecycleOwner,
                googleViewModel,
            )
        _rideOptionsBottomSheet = RideOptionsBottomSheet(
            WeakReference(view),
            requireContext(),
            requireActivity(),
            viewLifecycleOwner,
            WeakReference(nearbyVehicles)
        )
    }

    private fun initializeBottomSheetViews() {
        bottomSheetView = binding.root.findViewById<LinearLayout>(R.id.bottom_sheet)
        pickupTextView = binding.root.findViewById<EditText>(R.id.ti_pickup)
        dropOffTextView = binding.root.findViewById<EditText>(R.id.ti_drop_off)
    }


    private fun showUserLocation() {
        checkLocationPermission(null) {
            FetchLocation.getCurrentLocation(this@PickUpMapFragment, requireContext()) { location ->
                currentLocation = location
                animateCameraToCurrentLocation(location)
            }
        }
    }

    private fun requestLocationPermission() {
        checkLocationPermission("Need Access to Location") {
            showUserLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        _rideOptionsBottomSheet?.initializeGoogleMap(WeakReference(googleMap))
        nearbyVehicles?.startObservingNearbyVehicles(WeakReference(googleMap))
        onAddCameraAndMoveListeners()
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                getCurrentMapStyle()
            )
        )
        ifNetworkOrGPSDisabled()
        editTextFocusChangeListener()
        initializeRouteHelper()
        enableMyLocation()
    }

    private fun enableMyLocation() {
        checkLocationPermission(null) {
            googleMap.isMyLocationEnabled = true
        }
    }

    private fun initializeRouteHelper() {
        RouteCreationHelper.initialize(
            WeakReference(bottomSheetManager),
            WeakReference(_rideOptionsBottomSheet),
            WeakReference(this),
            WeakReference(googleMap),
            WeakReference(requireContext()),
            WeakReference(googleViewModel),
            viewLifecycleOwner,
        )
        routeHelper = RouteCreationHelper.getInstance()
    }

    private fun animateCameraToCurrentLocation(lastKnownLocation: Location?) {
        if (googleMap != null) {
            val userLatLng = lastKnownLocation?.let { LatLng(it.latitude, it.longitude) }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng!!, 13.0f))
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        storeLocationOffline()
        cleanUpResources()
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    private fun setUpCurrentLocationButton() {
        binding.currLocationBtn.setOnClickListener {
            showUserLocation()
            hideUserLocationButton()
        }
    }

    private fun cleanUpResources() {
        _binding = null
        onRemoveCameraAndMoveListener()
        BottomSheetManager.destroyInstance()
        bottomSheetManager = null
        FetchLocation.cleanResources()
        RouteCreationHelper.destroyInstance()
        routeHelper = null
        _rideOptionsBottomSheet = null
        _compositeDisposable.dispose()
    }

    private val cameraMoveListener = OnCameraMoveListener {
        if (binding.currLocationBtn.visibility != View.VISIBLE) {
            fadeInUserLocationButton()
        }
//        socketViewModel.sendMessage("hello")

    }

    private val cameraIdleListener = OnCameraIdleListener {
        ifNetworkOrGPSDisabled {
            if (!it) {
                if (!isPopulatingLocation) {
                    fetchLocation()
                }
            }
        }
        if (routeHelper != null) {
            routeHelper?.onCameraIdle()
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

    private fun getCurrentMapStyle(): Int =
        if (CheckMode.isDarkMode(requireContext())) R.raw.night_map else R.raw.uber_style


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
            routeHelper?.deleteEveryThingOnMap()
            showLocationPickerMarker()
            if (!_areListenersRegistered) {
                onAddCameraAndMoveListeners()
            }
            requestEditTextDropOffFocus()

        }
    }

    private fun requestEditTextDropOffFocus() {
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
                        googleViewModel.setPickUpLocationName(
                            googleMap.cameraPosition.target.latitude,
                            googleMap.cameraPosition.target.longitude
                        )
                    } else if (isDropOffEtInFocus) {
                        googleViewModel.setDropOffLocationName(
                            googleMap.cameraPosition.target.latitude,
                            googleMap.cameraPosition.target.longitude
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
                    googleViewModel.setPickUpLocationName(
                        location!!.latitude, location.longitude
                    )
                }
            }

        }


    }

    private fun animateToPickUpLocation() {
        val locationMapper = FetchLocation.customLocationMapper(
            googleViewModel.pickUpLatitude, googleViewModel.pickUpLongitude
        )
        runCatching { animateCameraToCurrentLocation(locationMapper) }

    }

    private fun animateToDropOffLocation() {
        val locationMapper = FetchLocation.customLocationMapper(
            googleViewModel.dropOffLatitude,
            googleViewModel.dropOffLongitude
        )


        runCatching { animateCameraToCurrentLocation(locationMapper) }

    }

    private fun createRoute(
        pickUpLatLng: LatLng? = null,
        dropOffLatLng: LatLng? = null
    ) {
        val pickUp = pickUpLatLng ?: LatLng(
            googleViewModel.pickUpLatitude,
            googleViewModel.pickUpLongitude,
        )
        val dropOff = dropOffLatLng ?: LatLng(
            googleViewModel.dropOffLatitude,
            googleViewModel.dropOffLongitude
        )

        hideLocationPickerMarker()
        onRemoveCameraAndMoveListener()
        routeHelper?.createRoute(
            LatLng(
                pickUp.longitude,
                pickUp.latitude
            ),
            LatLng(
                dropOff.longitude,
                dropOff.latitude
            )
        )
        locationViewModel.setPickUpLocation(pickUp)
        showRideOptionsBottomSheet()
    }

    private fun hideLocationPickerMarker() {
        binding.activityMainCenterLocationPin.visibility = View.GONE
    }


    private fun showRideOptionsBottomSheet() {
        _rideOptionsBottomSheet?.showBottomSheet()
    }


    fun showLocationPickerMarker() {
        if (binding.activityMainCenterLocationPin.visibility != View.VISIBLE)
            binding.activityMainCenterLocationPin.visibility = View.VISIBLE
    }


    fun onAddCameraAndMoveListeners() {

        if (!_areListenersRegistered) {
            _areListenersRegistered = true
            googleMap.setOnCameraIdleListener(cameraIdleListener)
            googleMap.setOnCameraMoveListener(cameraMoveListener)
        }
    }

    private fun onRemoveCameraAndMoveListener() {
        if (_areListenersRegistered) {
            _areListenersRegistered = false
            googleMap.setOnCameraIdleListener(null)
            googleMap.setOnCameraMoveListener(null)
        }
    }

    override fun createRouteAction(
        pickUpLatLng: LatLng?,
        dropOffLatLng: LatLng?
    ) {
        createRoute(pickUpLatLng, dropOffLatLng)
    }


    private fun storeLocationOffline() {
        lifecycleScope.launch {
            async {
                mapboxViewModel.saveCurrentLocationToDB(
                    com.example.uber.data.local.location.entities.Location(
                        location = LatLng(
                            currentLocation?.latitude!!,
                            currentLocation?.longitude!!
                        )
                    )
                )
            }.await()
        }
    }

    private inline fun ifNetworkOrGPSDisabled(dispatcher: (Boolean) -> Unit = {}) {
        val isNetworkOrGPSDisabled = !SystemInfo.isLocationEnabled(requireContext()) ||
                !SystemInfo.CheckInternetConnection(requireContext())

        if (isNetworkOrGPSDisabled) {
            googleViewModel.setLatitudeAndLongitudeIfNoNetworkOrGPS()
            dispatcher(true)
        } else {
            dispatcher(false)
        }
    }

}






