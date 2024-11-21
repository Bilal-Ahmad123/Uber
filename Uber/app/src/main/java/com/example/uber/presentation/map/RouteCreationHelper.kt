package com.example.uber.presentation.map

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.amalbit.trail.Route
import com.amalbit.trail.RouteOverlayView
import com.amalbit.trail.RouteOverlayView.RouteType
import com.example.uber.R
import com.example.uber.presentation.bottomSheet.BottomSheetManager
import com.example.uber.presentation.bottomSheet.RideOptionsBottomSheet
import com.example.uber.presentation.viewModels.GoogleViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.PolyUtil
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.plugins.annotation.LineManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.lang.ref.WeakReference


class RouteCreationHelper(
    private var bottomSheetManager: WeakReference<BottomSheetManager>,
    private var rideOptionsBottomSheet: WeakReference<RideOptionsBottomSheet>,
    private var pickUpMapFragment: WeakReference<PickUpMapFragment>,
    private var map: WeakReference<GoogleMap>,
    private var context: WeakReference<Context>,
    private var googleViewModel: WeakReference<GoogleViewModel>,
    private val viewLifecycleOwner: LifecycleOwner,
    private val mRouteOverlayView: WeakReference<RouteOverlayView>
) {

    private var mCouroutineScope: CoroutineScope? = CoroutineScope(Dispatchers.IO)
    private var _duration: Int = 0
    private var lineManager: LineManager? = null
    private var symbolManager: SymbolManager? = null
    private var _geometry: String? = null

    companion object {
        @Volatile
        private var instance: RouteCreationHelper? = null
        fun getInstance(): RouteCreationHelper? {
            return instance
        }

        fun initialize(
            bottomSheetManager: WeakReference<BottomSheetManager>,
            rideOptionsBottomSheet: WeakReference<RideOptionsBottomSheet>,
            pickUpMapFragment: WeakReference<PickUpMapFragment>,
            map: WeakReference<GoogleMap>,
            context: WeakReference<Context>,
            googleViewModel: WeakReference<GoogleViewModel>,
            viewLifecycleOwner: LifecycleOwner,
            mRouteOverlayView: WeakReference<RouteOverlayView>
        ): RouteCreationHelper? {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = RouteCreationHelper(
                            bottomSheetManager, rideOptionsBottomSheet, pickUpMapFragment,
                            map, context, googleViewModel, viewLifecycleOwner, mRouteOverlayView
                        )
                    }
                }
            }
            return instance
        }

        fun destroyInstance() {
            if (instance != null) {
                instance = null
            }
        }
    }


    fun createRoute(
        pickUpLocation: LatLng,
        dropOffLocation: LatLng,
    ) {
        googleViewModel.get()?.directionsRequest(pickUpLocation, dropOffLocation)
        observeDirectionsResponse()

//        mCouroutineScope?.launch {
//            val originPoint =
//                Point.fromLngLat(pickUpLocation.longitude(), pickUpLocation.latitude())
//            val destinationPoint =
//                Point.fromLngLat(dropOffLocation.longitude(), dropOffLocation.latitude())
//            requestRoute(originPoint, destinationPoint)
//        }


    }

    private fun observeDirectionsResponse() {
        googleViewModel.get()?.run {
            directions.observe(viewLifecycleOwner) {
                createAnimatedRoute(it.data!!.routes[0].overview_polyline!!.points)
            }
        }
    }

    private fun decodePolyLine(line: String): List<LatLng> {
        return PolyUtil.decode(line)
    }

    private fun createAnimatedRoute(line: String) {
        addMarkerToRouteStartAndRouteEnd()
        Route.Builder(mRouteOverlayView.get())
            .setRouteType(RouteType.PATH)
            .setCameraPosition(map.get()?.getCameraPosition())
            .setProjection(map.get()?.getProjection())
            .setLatLngs(decodePolyLine(line))
            .setBottomLayerColor(Color.YELLOW)
            .setTopLayerColor(Color.RED)
            .create()
    }

//    private fun requestRoute(originPoint: Point, destinationPoint: Point) {
//        val directionsCall = MapboxDirections.builder()
//            .origin(originPoint)
//            .destination(destinationPoint)
//            .overview(DirectionsCriteria.OVERVIEW_FULL)
//            .profile(DirectionsCriteria.PROFILE_DRIVING)
//            .accessToken(BuildConfig.MAPBOX_TOKEN)
//            .build()
//
//        runCatching {
//
//            directionsCall.enqueueCall(object : Callback<DirectionsResponse> {
//                override fun onResponse(
//                    call: Call<DirectionsResponse>,
//                    response: Response<DirectionsResponse>
//                ) {
//                    if (response.isSuccessful) {
//                        val routes: MutableList<DirectionsRoute>? = response.body()?.routes()
//                        Log.d("Route", "Response: ${response.body()}")
//                        if (!routes.isNullOrEmpty()) {
//                            val route = routes[0]
//                            _geometry = route.geometry()
//                            mCouroutineScope?.launch {
//                                _duration = getDurationInMinutes(routes[0].duration()!!)
//                                displayRoute(
//                                    route,
//                                    originPoint.latitude(),
//                                    originPoint.longitude(),
//                                    destinationPoint.latitude(),
//                                    destinationPoint.longitude(),
//                                )
//                            }
//                        } else {
//                            Log.e("Route Error", "No routes found")
//                        }
//                    } else {
//                        Log.e("Route Error", "Error: ${response.message()}")
//                    }
//                }
//
//                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
//                }
//
//            })
//        }.onFailure {
//            Log.e("Route Error", "Error: ${it.message}")
//        }
//
//    }


//    private suspend fun displayRoute(
//        route: DirectionsRoute,
//        pickUpLatitude: Double,
//        pickUpLongitude: Double,
//        dropOffLatitude: Double,
//        dropOffLongitude: Double,
//    ) {
//        val lineString = LineString.fromPolyline(route.geometry()!!, 6)
//
//        withContext(Dispatchers.Main) {
//            lineManager = LineManager(mapView.get()!!, map.get()!!, map.get()!!.style!!)
//
//
//            addMarkerIconsToStyle(map.get()!!.style!!)
//            addMarkerAnnotationToStyle(map.get()!!.style!!)
//            val lineOptions = LineOptions()
//                .withLineColor(Color.parseColor("#3887BE").toString())
//                .withLineWidth(3f)
//            lineManager?.create(lineOptions.withGeometry(lineString))
//            addMarkerToRouteStartAndRouteEnd(
//                pickUpLatitude,
//                pickUpLongitude,
//                dropOffLatitude,
//                dropOffLongitude
//            )
//            addMarkerAnnotation(pickUpLatitude, pickUpLongitude, dropOffLatitude, dropOffLongitude)
//            animateMapToFitRoutes()
//        }
//    }

    private fun addMarkerToRouteStartAndRouteEnd(
    ) {
//        symbolManager = SymbolManager(mapView.get()!!, map.get()!!, map.get()!!.style!!)
//        addAnnotationClickListener()
//        val symbolOptionsPickUp = SymbolOptions()
//            .withLatLng(LatLng(pickUpLatitude, pickUpLongitude))
//            .withIconImage("pickup-marker")
//            .withIconSize(1.3f)
//        symbolManager?.create(symbolOptionsPickUp)
//
//        val symbolOptionsDropOff = SymbolOptions()
//            .withLatLng(LatLng(dropOffLatitude, dropOffLongitude))
//            .withIconImage("dropoff-marker")
//            .withIconSize(1.3f)
//        symbolManager?.create(symbolOptionsDropOff)
        pickUpMarker()
        dropOffMarker()


    }

    private fun pickUpMarker() {
        map.get()?.addMarker(
            MarkerOptions()
                .position(
                    LatLng(
                        googleViewModel.get()!!.pickUpLatitude,
                        googleViewModel.get()!!.pickUpLongitude
                    )
                )
                .title("Melbourne")
                .snippet("Population: 4,137,400")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle))
        )
    }

    private fun dropOffMarker() {
        map.get()?.addMarker(
            MarkerOptions()
                .position(
                    LatLng(
                        googleViewModel.get()!!.dropOffLatitude,
                        googleViewModel.get()!!.dropOffLongitude
                    )
                )
                .title("Melbourne")
                .snippet("Population: 4,137,400")
                .icon(BitmapDescriptorFactory.fromResource(BitmapFactory.decodeResource(context.get()!!.resources, R.drawable.box)))
        )
    }

//    private fun addMarkerAnnotation(
//        pickUpLatitude: Double,
//        pickUpLongitude: Double,
//        dropOffLatitude: Double,
//        dropOffLongitude: Double
//    ) {
//        symbolManager?.iconAllowOverlap = true
//        symbolManager?.iconIgnorePlacement = true
//        val symbolOptionsPickUpAnnotation = SymbolOptions()
//            .withLatLng(LatLng(pickUpLatitude, pickUpLongitude))
//            .withIconImage("pickup-marker-annotation")
//            .withIconOffset(arrayOf(0f, -30f))
//
//
//        symbolManager?.create(symbolOptionsPickUpAnnotation)
//
//        val symbolOptionsDropOffAnnotation = SymbolOptions()
//            .withLatLng(LatLng(dropOffLatitude, dropOffLongitude))
//            .withIconImage("dropoff-marker-annotation")
//            .withIconOffset(arrayOf(0f, -30f))
//        symbolManager?.create(symbolOptionsDropOffAnnotation)
//    }


//    private fun addMarkerIconsToStyle(style: Style) {
//        val pickupIcon = BitmapUtils.getBitmapFromDrawable(
//            ContextCompat.getDrawable(mapView.get()?.context!!, R.drawable.circle)
//        )
//        val dropoffIcon = BitmapUtils.getBitmapFromDrawable(
//            ContextCompat.getDrawable(mapView.get()?.context!!, R.drawable.box)
//        )
//        if (pickupIcon != null && dropoffIcon != null) {
//            style.addImage("pickup-marker", scaleBitMapImageSize(pickupIcon)!!)
//            style.addImage("dropoff-marker", scaleBitMapImageSize(dropoffIcon)!!)
//        }
//    }

//    private fun addMarkerAnnotationToStyle(style: Style) {
//
//        val pickupMarker =
//            createTextMarkerDrawable(
//                context.get()!!,
//                "(${_duration} MIN) " + mapboxViewModel.get()?.pickUpLocationName?.value.toString() + "  >  "
//            )
//        val dropoffMarker = createTextMarkerDrawable(
//            context.get()!!,
//            mapboxViewModel.get()?.dropOffLocationName?.value.toString() + "  >  "
//        )
//        style.addImage("pickup-marker-annotation", pickupMarker)
//        style.addImage("dropoff-marker-annotation", dropoffMarker)
//
//    }

//    private fun scaleBitMapImageSize(icon: Bitmap): Bitmap? {
//        val scaledIcon = icon?.let {
//            Bitmap.createScaledBitmap(it, 20, 20, false)
//        }
//        return scaledIcon
//    }

//    private fun createTextMarkerDrawable(
//        context: Context,
//        text: String
//    ): Bitmap {
//        val shapeDrawable = ContextCompat.getDrawable(context, R.drawable.marker_annotations)!!
//        val paint = Paint().apply {
//            color = if (CheckMode.isDarkMode(context)) Color.WHITE else Color.BLACK
//            textSize = 30f
//            isAntiAlias = true
//            textAlign = Paint.Align.CENTER
//            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
//        }
//        val rect = Rect();
//        paint.getTextBounds(text, 0, text.length, rect)
//        val height = 80
//        shapeDrawable.setBounds(0, 0, rect.width() + 20, height)
//
//        val bitmap = Bitmap.createBitmap(rect.width() + 20, height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//
//        shapeDrawable.draw(canvas)
//
//        val xPos = canvas.width / 2f
//        val yPos = (canvas.height / 2f) - (paint.descent() + paint.ascent()) / 2
//        canvas.drawText(text, xPos, yPos, paint)
//
//        return bitmap
//    }

//    private fun getDurationInMinutes(seconds: Double): Int {
//        val minutes = (seconds % 3600) / 60
//        return if (minutes.toInt() == 0) {
//            1
//        } else {
//            minutes.toInt()
//        }
//    }

//    fun deleteRoute() {
//        map.get()?.style?.removeImage("pickup-marker")
//        map.get()?.style?.removeImage("dropoff-marker")
//        map.get()?.style?.removeImage("pickup-marker-annotation")
//        map.get()?.style?.removeImage("dropoff-marker-annotation")
//        lineManager?.deleteAll()
//        lineManager = null
//        symbolManager?.deleteAll()
//    }

//    fun doesLineManagerExist(): Boolean {
//        return lineManager != null
//    }

//    fun clearResources() {
//        mCouroutineScope = null
//        lineManager = null
//        symbolManager = null
//        destroyInstance()
//    }

//    private fun addAnnotationClickListener() {
//        symbolManager?.addClickListener {
//            if (it.iconImage == "pickup-marker-annotation") {
//                bottomSheetManager.get()?.showBottomSheet(Markers.PICK_UP)
//            } else {
//                bottomSheetManager.get()?.showBottomSheet(Markers.DROP_OFF)
//            }
//            rideOptionsBottomSheet.get()?.hideBottomSheet()
//            deleteRoute()
//            pickUpMapFragment.get()?.showLocationPickerMarker()
//            pickUpMapFragment.get()?.onAddCameraAndMoveListeners()
//        }
//    }

//    private fun decodeGeometryStringToRoutes(): LatLngBounds {
//        val lineString = LineString.fromPolyline(_geometry!!, 6)
//        val latLngList = lineString.coordinates().map { point ->
//            LatLng(point.latitude(), point.longitude())
//        }
//        val latLngBounds = LatLngBounds.Builder()
//            .includes(latLngList)
//            .build()
//        return latLngBounds
//    }

    private var latLngBounds: LatLngBounds? = null

//    private fun animateMapToFitRoutes() {
//        mCouroutineScope?.launch(Dispatchers.IO) {
//            latLngBounds = decodeGeometryStringToRoutes()
//            withContext(Dispatchers.Main) {
//                animateToRespectivePadding()
//            }
//        }
//
//    }

//    fun animateToRespectivePadding(padding: Int = 500) {
//        if (map.get() != null && latLngBounds != null) {
//            val paddingTop = 100
//            val paddingLeft = 100
//            val paddingRight = 100
//            map.get()!!.animateCamera(
//                CameraUpdateFactory.newLatLngBounds(
//                    latLngBounds!!,
//                    paddingLeft,
//                    paddingTop,
//                    paddingRight,
//                    padding
//                )
//            )
//        }
//    }

//    private fun setMapZoomLevel() {
//        map.get()?.cameraPosition = CameraPosition.Builder().zoom(1.00).build()
//    }

    fun directionsApiResult() {

    }

    private fun decodePolyline() {

    }


}

