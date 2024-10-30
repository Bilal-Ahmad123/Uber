package com.example.uber.presentation.map

//import com.example.uber.core.utils.FetchLocation
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.mapbox.common.MapboxOptions
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
import java.lang.ref.WeakReference

import kotlin.math.abs

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
    private var hasCameraChanged = true


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
        mapMoveListener()
        requestLocationPermission()
        setupListeners()
        map.addOnMapLoadedListener {
            editTextFocusChangeListener()
        }

        setBackButtonOnClickListener()




        if (isAdded) {
            setUpCurrentLocationButton()
//            requestLocationPermission()
            getInitialPickUpLocation()
        }

    }

    private fun showUserLocation() {
        checkLocationPermission(null) {
            FetchLocation.getCurrentLocation(requireContext()) { location ->
                animateCameraToCurrentLocation(
                    FetchLocation.customLocationMapper(
                        location!!.latitude(), location.longitude()
                    )
                )
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
        mapboxMap.setStyle(getCurrentMapStyle())
        mapboxMap.addOnMoveListener(moveListener)
        mapboxMap.addOnCameraIdleListener(cameraPositionChangeListener)
        routeHelper = RouteCreationHelper(WeakReference(binding.mapView), WeakReference(mapboxMap),requireContext())
    }

    private fun animateCameraToCurrentLocation(lastKnownLocation: Location?) {
        hasCameraChanged = true
        if (lastKnownLocation != null) {
            val cameraPosition = CameraOptions.Builder()
                .center(Point.fromLngLat(lastKnownLocation.longitude, lastKnownLocation.latitude))
                .zoom(13.0)
                .build()

            mapView.camera.easeTo(cameraPosition, mapAnimationOptions {
                duration(1)
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
        hideUserLocationButton()
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
                        Log.d("DropOffLocation", "${map.cameraState.center.latitude()}, ${map.cameraState.center.longitude()}")
                        dropOffLocationViewModel.setPickUpLocationName(
                            map.cameraState.center.latitude(),
                            map.cameraState.center.longitude()
                        )

                    }
                } finally {
                    isPopulatingLocation = false
                }
            }
            hasCameraChanged = false
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
            FetchLocation.getCurrentLocation(
                requireContext()
            ) { location ->
                runCatching {
                    pickUpLocationViewModel.setPickUpLocationName(
                        location!!.latitude(), location.longitude()
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


    private val debounceDuration = 300L
    private val handler = Handler(Looper.getMainLooper())
    private val activeAnimators = mutableSetOf<CameraAnimatorType>()


    private val idleRunnable = Runnable {
        Log.d("CameraIdle", "Camera is idle.")
        fetchLocation()
    }

    private fun setupListeners() {
        // Set up animation lifecycle listener
        mapView.camera.addCameraAnimationsLifecycleListener(object :
            CameraAnimationsLifecycleListener {
            override fun onAnimatorStarting(
                type: CameraAnimatorType,
                animator: ValueAnimator,
                owner: String?
            ) {
                activeAnimators.add(type)
                handler.removeCallbacks(idleRunnable)
            }

            override fun onAnimatorEnding(
                type: CameraAnimatorType,
                animator: ValueAnimator,
                owner: String?
            ) {
                activeAnimators.remove(type)
                checkIdleState()
            }

            override fun onAnimatorCancelling(
                type: CameraAnimatorType,
                animator: ValueAnimator,
                owner: String?
            ) {
                activeAnimators.remove(type)
                checkIdleState()
            }

            override fun onAnimatorInterrupting(
                type: CameraAnimatorType,
                runningAnimator: ValueAnimator,
                runningAnimatorOwner: String?,
                newAnimator: ValueAnimator,
                newAnimatorOwner: String?
            ) {
                activeAnimators.remove(type)
                checkIdleState()
            }
        })

    }

    private fun mapMoveListener(){
        mapView.gestures.addOnMoveListener(object : OnMoveListener {
            override fun onMoveBegin(detector: MoveGestureDetector) {
                handler.removeCallbacks(idleRunnable)
                fadeInUserLocationButton()
            }

            override fun onMove(detector: MoveGestureDetector): Boolean = false

            override fun onMoveEnd(detector: MoveGestureDetector) {
            }
        })
    }

    private fun checkIdleState() {
        if (activeAnimators.isEmpty()) {
            handler.postDelayed(idleRunnable, debounceDuration)
        }
    }
}






