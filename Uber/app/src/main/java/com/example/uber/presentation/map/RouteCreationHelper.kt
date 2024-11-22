package com.example.uber.presentation.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.amalbit.trail.Route
import com.amalbit.trail.RouteOverlayView
import com.amalbit.trail.RouteOverlayView.RouteType
import com.example.uber.R
import com.example.uber.presentation.bottomSheet.BottomSheetManager
import com.example.uber.presentation.bottomSheet.RideOptionsBottomSheet
import com.example.uber.presentation.viewModels.GoogleViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.PolyUtil
import com.logicbeanzs.uberpolylineanimation.MapAnimator
import com.mapbox.mapboxsdk.plugins.annotation.LineManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    }

    private fun observeDirectionsResponse() {
        googleViewModel.get()?.run {
            directions.observe(viewLifecycleOwner) {
                if(it.data!!.routes.isNotEmpty()) {
                    createAnimatedRoute(it.data!!.routes[0].overview_polyline!!.points)
                }
            }
        }
    }

    private fun decodePolyLine(line: String): List<LatLng> {
        return PolyUtil.decode(line)
    }

    @SuppressLint("ResourceType")
    private fun createAnimatedRoute(line: String) {
        val routePoints:List<LatLng> = decodePolyLine(line)
        addMarkerToRouteStartAndRouteEnd()
        MapAnimator.animateRoute(map.get()!!, routePoints)
        MapAnimator.setPrimaryLineColor(Color.parseColor("#000000"))
        MapAnimator.setSecondaryLineColor(Color.parseColor("#ffffff"))
        animateCameraToFillRoute(routePoints)
    }

    private fun animateCameraToFillRoute(routePoints:List<LatLng>){
        val bounds = LatLngBounds.Builder()
        CoroutineScope(Dispatchers.Default).launch{
            routePoints.forEach {
                bounds.include(it)
            }
            withContext(Dispatchers.Main) {
                map.get()?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 500))
            }
        }
    }


    private fun addMarkerToRouteStartAndRouteEnd(
    ) {
        pickUpMarker()
        dropOffMarker()
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
                .icon(bitmapDescriptorFromVector(context.get()!!, R.drawable.box))
        )
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
                .icon(bitmapDescriptorFromVector(context.get()!!, R.drawable.circle))
        )
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            30,
           30
        )
        val bitmap = Bitmap.createBitmap(
           30,
           30,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable!!.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
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



    private fun scaleBitMapImageSize(icon: Bitmap): Bitmap? {
        val scaledIcon = icon?.let {
            Bitmap.createScaledBitmap(it, 20, 20, false)
        }
        return scaledIcon
    }

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

    fun deleteRoute() {
        map.get()?.clear()
    }


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

