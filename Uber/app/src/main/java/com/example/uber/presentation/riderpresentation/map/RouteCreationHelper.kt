package com.example.uber.presentation.riderpresentation.map

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.uber.R
import com.example.uber.core.enums.Markers
import com.example.uber.presentation.riderpresentation.bottomSheet.BottomSheetManager
import com.example.uber.presentation.riderpresentation.bottomSheet.RideOptionsBottomSheet
import com.example.uber.presentation.riderpresentation.map.utils.ShowNearbyVehicleService
import com.example.uber.presentation.riderpresentation.viewModels.GoogleViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.logicbeanzs.uberpolylineanimation.MapAnimator
import dagger.hilt.android.internal.managers.ViewComponentManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.util.Arrays


class RouteCreationHelper(
    private var bottomSheetManager: WeakReference<BottomSheetManager>,
    private var rideOptionsBottomSheet: WeakReference<RideOptionsBottomSheet>,
    private var pickUpMapFragment: WeakReference<PickUpMapFragment>,
    private var map: WeakReference<GoogleMap>,
    private var context: WeakReference<Context>,
    private var googleViewModel: WeakReference<GoogleViewModel>,
    private val viewLifecycleOwner: LifecycleOwner,
) : OnMarkerClickListener {

    private var pickUpMarker: Marker? = null
    private var dropOffMarker: Marker? = null

    companion object {
        var latLngBounds:List<LatLng>? = null

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
        ): RouteCreationHelper? {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = RouteCreationHelper(
                            bottomSheetManager, rideOptionsBottomSheet, pickUpMapFragment,
                            map, context, googleViewModel, viewLifecycleOwner
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
                Log.d("observeDirectionsResponse", "observeDirectionsResponse: ${it.data}")
                if (it.data!!.routes.isNotEmpty()) {

                    createAnimatedRoute(it.data!!.routes[0].overview_polyline!!.points)
                } else {
                    showCurvedPolyline(
                        LatLng(
                            googleViewModel.get()!!.pickUpLatitude,
                            googleViewModel.get()!!.pickUpLongitude
                        ), LatLng(
                            googleViewModel.get()!!.dropOffLatitude,
                            googleViewModel.get()!!.dropOffLongitude
                        ), 0.5
                    )
                }
            }
        }
    }

    private fun decodePolyLine(line: String): List<LatLng> {
        return PolyUtil.decode(line)
    }

    @SuppressLint("ResourceType")
    private fun createAnimatedRoute(line: String) {
        val routePoints: List<LatLng> = decodePolyLine(line)
        latLngBounds = routePoints
        addMarkerToRouteStartAndRouteEnd()
        MapAnimator.animateRoute(map.get()!!, routePoints)
        MapAnimator.setPrimaryLineColor(Color.parseColor("#000000"))
        MapAnimator.setSecondaryLineColor(Color.parseColor("#ffffff"))
        animateCameraToFillRoute(routePoints)
        setUpMarkerClickListener()
    }

    private var bounds:LatLngBounds.Builder? = LatLngBounds.Builder()

    private fun animateCameraToFillRoute(routePoints: List<LatLng>) {
        bounds = LatLngBounds.Builder()
        CoroutineScope(Dispatchers.Default).launch {
            routePoints.forEach {
                bounds!!.include(it)
            }
            withContext(Dispatchers.Main) {
                map.get()?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds!!.build(), 500))
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
                .icon(bitmapDescriptorFromVector(context.get()!!, R.drawable.box))
        )
        createAnnotation(
            googleViewModel.get()?.dropOffLatitude!!,
            googleViewModel.get()?.dropOffLongitude!!,
            Markers.DROP_OFF,
            googleViewModel.get()?.dropOffLocationName?.value.toString()
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
                .icon(bitmapDescriptorFromVector(context.get()!!, R.drawable.circle))
        )
        createAnnotation(
            googleViewModel.get()?.pickUpLatitude!!,
            googleViewModel.get()?.pickUpLongitude!!,
            Markers.PICK_UP,
            googleViewModel.get()?.pickUpLocationName?.value.toString()
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

    fun deleteEveryThingOnMap() {
        map.get()?.clear()
        ShowNearbyVehicleService.drivers.clear()
        pickUpMarker = null
        dropOffMarker = null
    }

    private fun createAnnotation(
        latitude: Double,
        longitude: Double,
        marker: Markers,
        text: String
    ) {
        val marker_view: View =
            (context.get()
                ?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.custom_marker,
                null
            )
        val addressSrc =
            marker_view.findViewById<View>(com.example.uber.R.id.addressTxt) as TextView
        val etaTxt = marker_view.findViewById<View>(com.example.uber.R.id.etaTxt) as TextView
        addressSrc.text = text
        etaTxt.text = "10"
        if (marker == Markers.PICK_UP) {
            etaTxt.visibility = View.VISIBLE
        }
        val marker_opt_source = MarkerOptions().position(
            LatLng(
                latitude,
                longitude
            )
        )
        marker_opt_source.icon(
            BitmapDescriptorFactory.fromBitmap(
                createDrawableFromView(
                    context.get()!!,
                    marker_view
                )
            )
        ).anchor(0.00f, 0.20f);
        if (marker == Markers.PICK_UP) {
            pickUpMarker = map.get()?.addMarker(marker_opt_source);
            pickUpMarker?.tag = "pickUp"
        } else {
            dropOffMarker = map.get()?.addMarker(marker_opt_source);
            dropOffMarker?.tag = "dropOff"
        }
    }

    private fun createDrawableFromView(context: Context, view: View): Bitmap {
        val mContext =
            if (context is ViewComponentManager.FragmentContextWrapper) context.baseContext else context
        val displayMetrics = DisplayMetrics()
        (mContext as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        view.layoutParams =
            RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.buildDrawingCache()
        val bitmap =
            Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        view.draw(canvas)

        return bitmap
    }


//    fun clearResources() {
//        mCouroutineScope = null
//        lineManager = null
//        symbolManager = null
//        destroyInstance()
//    }

    private fun addAnnotationClickListener(marker: Marker) {
        if(marker.tag != null) {
            if (marker.tag?.equals(pickUpMarker?.tag)!!) {

                bottomSheetManager.get()?.showBottomSheet(Markers.PICK_UP)
            } else if (marker.tag?.equals(dropOffMarker?.tag)!!) {
                bottomSheetManager.get()?.showBottomSheet(Markers.DROP_OFF)
            }
            rideOptionsBottomSheet.get()?.hideBottomSheet()
            deleteEveryThingOnMap()
            pickUpMapFragment.get()?.showLocationPickerMarker()
            pickUpMapFragment.get()?.onAddCameraAndMoveListeners()
        }
    }


//
//    private fun animateMapToFitRoutes() {
//        mCouroutineScope?.launch(Dispatchers.IO) {
//            latLngBounds = decodeGeometryStringToRoutes()
//            withContext(Dispatchers.Main) {
//                animateToRespectivePadding()
//            }
//        }
//
//    }

    fun animateToRespectivePadding(padding: Int = 500) {
        if (map.get() != null && latLngBounds != null) {
            val paddingTop = 100
            val paddingLeft = 100
            val paddingRight = 100
            val builder = LatLngBounds.Builder()
            val width:Int? = context.get()?.resources?.displayMetrics?.widthPixels;
           val height:Int? = context.get()?.resources?.displayMetrics?.heightPixels;
            map.get()?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds!!.build(),width!!,height!!, 900))
        }
    }

//    private fun setMapZoomLevel() {
//        map.get()?.cameraPosition = CameraPosition.Builder().zoom(1.00).build()
//    }

    fun directionsApiResult() {

    }

    private fun decodePolyline() {

    }

    private fun setUpMarkerClickListener() {
        map.get()?.setOnMarkerClickListener(this)
    }

    private fun dpToPx(context: Context, dpValue: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(dpValue * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    fun onCameraIdle() {
        if (pickUpMarker != null && context.get() != null) {
            val PickupPoint: Point = map.get()?.projection?.toScreenLocation(
                LatLng(
                    googleViewModel.get()?.pickUpLatitude!!,
                    googleViewModel.get()?.pickUpLongitude!!
                )
            )!!
            pickUpMarker!!.setAnchor(
                if (PickupPoint.x < dpToPx(context.get()!!, 200)) 0.00f else 1.00f,
                if (PickupPoint.y < dpToPx(context.get()!!, 100)) 0.20f else 1.20f
            )
        }
        if (dropOffMarker != null && context.get() != null) {
            val PickupPoint: Point = map.get()?.projection?.toScreenLocation(
                LatLng(
                    googleViewModel.get()?.dropOffLatitude!!,
                    googleViewModel.get()?.dropOffLongitude!!
                )
            )!!
            dropOffMarker!!.setAnchor(
                if (PickupPoint.x < dpToPx(context.get()!!, 200)) 0.00f else 1.00f,
                if (PickupPoint.y < dpToPx(context.get()!!, 100)) 0.20f else 1.20f
            )
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        addAnnotationClickListener(marker)

        return true
    }

    private fun showCurvedPolyline(p1: LatLng, p2: LatLng, k: Double) {
        //Calculate distance and heading between two points
        val d = SphericalUtil.computeDistanceBetween(p1, p2)
        val h = SphericalUtil.computeHeading(p1, p2)

        //Midpoint position
        val p = SphericalUtil.computeOffset(p1, d * 0.5, h)

        //Apply some mathematics to calculate position of the circle center
        val x = (1 - k * k) * d * 0.5 / (2 * k)
        val r = (1 + k * k) * d * 0.5 / (2 * k)

        val c = SphericalUtil.computeOffset(p, x, h + 90.0)

        //Polyline options
        val options: PolylineOptions = PolylineOptions()
        val pattern = Arrays.asList(Dash(30f), Gap(20f))

        //Calculate heading between circle center and two points
        val h1 = SphericalUtil.computeHeading(c, p1)
        val h2 = SphericalUtil.computeHeading(c, p2)

        //Calculate positions of points on circle border and add them to polyline options
        val numpoints = 100
        val step = (h2 - h1) / numpoints

        for (i in 0 until numpoints) {
            val pi = SphericalUtil.computeOffset(c, r, h1 + i * step)
            options.add(pi)
        }

        map.get()
            ?.addPolyline(options.width(10F).color(Color.MAGENTA).geodesic(false).pattern(pattern))
    }

}

