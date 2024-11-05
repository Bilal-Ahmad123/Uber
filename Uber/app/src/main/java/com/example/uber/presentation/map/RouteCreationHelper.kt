package com.example.uber.presentation.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.uber.BuildConfig
import com.example.uber.R
import com.example.uber.core.enums.Markers
import com.example.uber.presentation.bottomSheet.BottomSheetManager
import com.example.uber.presentation.bottomSheet.RideOptionsBottomSheet
import com.example.uber.presentation.viewModels.DropOffLocationViewModel
import com.example.uber.presentation.viewModels.PickUpLocationViewModel
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.LineManager
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.utils.BitmapUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference


object RouteCreationHelper {
    private lateinit var bottomSheetManager: WeakReference<BottomSheetManager>
    private lateinit var rideOptionsBottomSheet: WeakReference<RideOptionsBottomSheet>
    private lateinit var pickUpMapFragment: WeakReference<PickUpMapFragment>
    private lateinit var pickUpLocationViewModel: PickUpLocationViewModel
    private lateinit var dropOffLocationViewModel: DropOffLocationViewModel
    private lateinit var mapView: WeakReference<MapView>
    private lateinit var map: WeakReference<MapboxMap>
    private lateinit var context: WeakReference<Context>
    private var mCouroutineScope: CoroutineScope? = CoroutineScope(Dispatchers.IO)
    private var _duration: Int = 0
    private var lineManager: LineManager? = null
    private var symbolManager: SymbolManager? = null
    private var _geometry: String? = null
    private var cachedLatLngBounds: LatLngBounds? = null

    fun initInstances(
        mapView: WeakReference<MapView>,
        map: WeakReference<MapboxMap>,
        context: WeakReference<Context>,
        pickUpLocationViewModel: PickUpLocationViewModel,
        dropOffLocationViewModel: DropOffLocationViewModel,
        rideOptionsBottomSheet: WeakReference<RideOptionsBottomSheet>,
        bottomSheetManager: WeakReference<BottomSheetManager>,
        pickUpMapFragment: WeakReference<PickUpMapFragment>
    ) {
        this.map = map
        this.mapView = mapView
        this.context = context
        this.pickUpLocationViewModel = pickUpLocationViewModel
        this.dropOffLocationViewModel =dropOffLocationViewModel
        this.rideOptionsBottomSheet = rideOptionsBottomSheet
        this.bottomSheetManager = bottomSheetManager
        this.pickUpMapFragment = pickUpMapFragment
    }

    fun createRoute(
        pickUpLocation: Point,
        dropOffLocation: Point,
        ) {

        mCouroutineScope?.launch {
            val originPoint =
                Point.fromLngLat(pickUpLocation.longitude(), pickUpLocation.latitude())
            val destinationPoint =
                Point.fromLngLat(dropOffLocation.longitude(), dropOffLocation.latitude())
            requestRoute(originPoint, destinationPoint)
        }
    }

    private fun requestRoute(originPoint: Point, destinationPoint: Point) {
        val directionsCall = MapboxDirections.builder()
            .origin(originPoint)
            .destination(destinationPoint)
            .overview(DirectionsCriteria.OVERVIEW_FULL)
            .profile(DirectionsCriteria.PROFILE_DRIVING)
            .accessToken(BuildConfig.MAPBOX_TOKEN)
            .build()

        directionsCall.enqueueCall(object : Callback<DirectionsResponse> {
            override fun onResponse(
                call: Call<DirectionsResponse>,
                response: Response<DirectionsResponse>
            ) {
                if (response.isSuccessful) {
                    val routes: MutableList<DirectionsRoute>? = response.body()?.routes()
                    Log.d("Route", "Response: ${response.body()}")
                    if (!routes.isNullOrEmpty()) {
                        val route = routes[0]
                        _geometry = route.geometry()
                        mCouroutineScope?.launch {
                            _duration = getDurationInMinutes(routes[0].duration()!!)
                            displayRoute(
                                route,
                                originPoint.latitude(),
                                originPoint.longitude(),
                                destinationPoint.latitude(),
                                destinationPoint.longitude(),
                            )
                        }
                    } else {
                        Log.e("Route Error", "No routes found")
                    }
                } else {
                    Log.e("Route Error", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }


    private suspend fun displayRoute(
        route: DirectionsRoute,
        pickUpLatitude: Double,
        pickUpLongitude: Double,
        dropOffLatitude: Double,
        dropOffLongitude: Double,
    ) {
        val lineString = LineString.fromPolyline(route.geometry()!!, 6)

        withContext(Dispatchers.Main) {
            lineManager = LineManager(mapView.get()!!, map.get()!!, map.get()!!.style!!)


            addMarkerIconsToStyle(map.get()!!.style!!)
            addMarkerAnnotationToStyle(map.get()!!.style!!)
            val lineOptions = LineOptions()
                .withLineColor(Color.parseColor("#3887BE").toString())
                .withLineWidth(3f)
            lineManager?.create(lineOptions.withGeometry(lineString))
            addMarkerToRouteStartAndRouteEnd(
                pickUpLatitude,
                pickUpLongitude,
                dropOffLatitude,
                dropOffLongitude
            )
            addMarkerAnnotation(pickUpLatitude, pickUpLongitude, dropOffLatitude, dropOffLongitude)
            animateMapToFitRoutes()
        }
    }

    private fun addMarkerToRouteStartAndRouteEnd(
        pickUpLatitude: Double,
        pickUpLongitude: Double,
        dropOffLatitude: Double,
        dropOffLongitude: Double
    ) {
        symbolManager = SymbolManager(mapView.get()!!, map.get()!!, map.get()!!.style!!)
        addAnnotationClickListener()
        val symbolOptionsPickUp = SymbolOptions()
            .withLatLng(LatLng(pickUpLatitude, pickUpLongitude))
            .withIconImage("pickup-marker")
            .withIconSize(1.3f)
        symbolManager?.create(symbolOptionsPickUp)

        val symbolOptionsDropOff = SymbolOptions()
            .withLatLng(LatLng(dropOffLatitude, dropOffLongitude))
            .withIconImage("dropoff-marker")
            .withIconSize(1.3f)
        symbolManager?.create(symbolOptionsDropOff)
    }

    private fun addMarkerAnnotation(
        pickUpLatitude: Double,
        pickUpLongitude: Double,
        dropOffLatitude: Double,
        dropOffLongitude: Double
    ) {
        symbolManager?.iconAllowOverlap = true
        symbolManager?.iconIgnorePlacement = true
        val symbolOptionsPickUpAnnotation = SymbolOptions()
            .withLatLng(LatLng(pickUpLatitude, pickUpLongitude))
            .withIconImage("pickup-marker-annotation")
            .withIconOffset(arrayOf(0f, -30f))


        symbolManager?.create(symbolOptionsPickUpAnnotation)

        val symbolOptionsDropOffAnnotation = SymbolOptions()
            .withLatLng(LatLng(dropOffLatitude, dropOffLongitude))
            .withIconImage("dropoff-marker-annotation")
            .withIconOffset(arrayOf(0f, -30f))
        symbolManager?.create(symbolOptionsDropOffAnnotation)
    }


    private fun addMarkerIconsToStyle(style: Style) {
        val pickupIcon = BitmapUtils.getBitmapFromDrawable(
            ContextCompat.getDrawable(mapView.get()?.context!!, R.drawable.circle)
        )
        val dropoffIcon = BitmapUtils.getBitmapFromDrawable(
            ContextCompat.getDrawable(mapView.get()?.context!!, R.drawable.box)
        )
        if (pickupIcon != null && dropoffIcon != null) {
            style.addImage("pickup-marker", scaleBitMapImageSize(pickupIcon)!!)
            style.addImage("dropoff-marker", scaleBitMapImageSize(dropoffIcon)!!)
        }
    }

    private fun addMarkerAnnotationToStyle(style: Style) {

        val pickupMarker =
            createTextMarkerDrawable(
                context.get()!!,
                "(${_duration} MIN) " + pickUpLocationViewModel.locationName.value.toString() + "  >  "
            )
        val dropoffMarker = createTextMarkerDrawable(
            context.get()!!,
            dropOffLocationViewModel.locationName.value.toString() + "  >  "
        )
        style.addImage("pickup-marker-annotation", pickupMarker)
        style.addImage("dropoff-marker-annotation", dropoffMarker)

    }

    private fun scaleBitMapImageSize(icon: Bitmap): Bitmap? {
        val scaledIcon = icon?.let {
            Bitmap.createScaledBitmap(it, 20, 20, false)
        }
        return scaledIcon
    }

    private fun createTextMarkerDrawable(
        context: Context,
        text: String
    ): Bitmap {
        val shapeDrawable = ContextCompat.getDrawable(context, R.drawable.marker_annotations)!!
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 30f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val rect = Rect();
        paint.getTextBounds(text, 0, text.length, rect)
        val height = 80
        shapeDrawable.setBounds(0, 0, rect.width() + 20, height)

        val bitmap = Bitmap.createBitmap(rect.width() + 20, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        shapeDrawable.draw(canvas)

        val xPos = canvas.width / 2f
        val yPos = (canvas.height / 2f) - (paint.descent() + paint.ascent()) / 2
        canvas.drawText(text, xPos, yPos, paint)

        return bitmap
    }

    private fun getDurationInMinutes(seconds: Double): Int {
        val minutes = (seconds % 3600) / 60
        return if (minutes.toInt() == 0) {
            1
        } else {
            minutes.toInt()
        }
    }

    fun deleteRoute() {
        map.get()?.style?.removeImage("pickup-marker")
        map.get()?.style?.removeImage("dropoff-marker")
        map.get()?.style?.removeImage("pickup-marker-annotation")
        map.get()?.style?.removeImage("dropoff-marker-annotation")
        lineManager?.deleteAll()
        lineManager = null
        symbolManager?.deleteAll()
    }

    fun doesLineManagerExist(): Boolean {
        return lineManager != null
    }

    fun clearResources() {
        mCouroutineScope = null
        lineManager = null
        symbolManager = null
    }

    private fun addAnnotationClickListener() {
        symbolManager?.addClickListener {
            if (it.iconImage == "pickup-marker-annotation") {
                bottomSheetManager.get()?.showBottomSheet(Markers.PICK_UP)
            } else {
                bottomSheetManager.get()?.showBottomSheet(Markers.DROP_OFF)
            }
            rideOptionsBottomSheet.get()?.hideBottomSheet()
            deleteRoute()
            pickUpMapFragment.get()?.showLocationPickerMarker()
            pickUpMapFragment.get()?.onAddCameraAndMoveListeners()
        }
    }

    private fun decodeGeometryStringToRoutes(): LatLngBounds {
        val lineString = LineString.fromPolyline(_geometry!!, 6)
        val latLngList = lineString.coordinates().map { point ->
            LatLng(point.latitude(), point.longitude())
        }
        val latLngBounds = LatLngBounds.Builder()
            .includes(latLngList)
            .build()
        return latLngBounds
    }

    private var latLngBounds: LatLngBounds? = null

    private fun animateMapToFitRoutes() {
        mCouroutineScope?.launch(Dispatchers.IO) {
            latLngBounds = decodeGeometryStringToRoutes()
            withContext(Dispatchers.Main) {
                animateToRespectivePadding(200)
            }
        }

    }

    fun animateToRespectivePadding(padding: Int = 350){
        if(map.get() != null && latLngBounds != null) {
            map.get()?.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    latLngBounds!!,
                    padding
                )
            )
        }
    }


}

