package com.example.uber.presentation.riderpresentation.map.Routes

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
import androidx.lifecycle.lifecycleScope
import com.example.uber.R
import com.example.uber.core.enums.Markers
import com.example.uber.core.utils.BitMapCreator
import com.example.uber.presentation.riderpresentation.map.utils.CustomMapAnimator
import com.example.uber.presentation.riderpresentation.viewModels.GoogleViewModel
import com.example.uber.presentation.riderpresentation.viewModels.MapAndSheetsSharedViewModel
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
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import dagger.hilt.android.internal.managers.ViewComponentManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.util.Arrays


class RouteCreationHelper(
    private var map: WeakReference<GoogleMap>,
    private var context: WeakReference<Context>,
    private var googleViewModel: WeakReference<GoogleViewModel>,
    private var sharedViewModel: WeakReference<MapAndSheetsSharedViewModel>,
    private val viewLifecycleOwner: LifecycleOwner,
) : OnMarkerClickListener {

    private var foregroundPolyline: Polyline? = null
    private var backgroundPolyline: Polyline? = null
    private var pickUpMarker: Marker? = null
    private var circularMarker:Marker? = null
    private var boxMarker:Marker? = null

    private var dropOffMarker: Marker? = null

    companion object {
        var latLngBounds: List<LatLng>? = null
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
            viewLifecycleOwner.lifecycleScope.launch {
                directions.collectLatest { a->
                if (a?.data!!.routes.isNotEmpty()) {
                    createAnimatedRoute(a.data!!.routes[0].overview_polyline!!.points)
                } else {
//                    showCurvedPolyline(
//                        LatLng(
//                            googleViewModel.get()!!.pickUpLatitude,
//                            googleViewModel.get()!!.pickUpLongitude
//                        ), LatLng(
//                            googleViewModel.get()!!.dropOffLatitude,
//                            googleViewModel.get()!!.dropOffLongitude
//                        ), 0.5
//                    )
                }
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
        if (routePoints.size > 1) {
            latLngBounds = routePoints
            addMarkerToRouteStartAndRouteEnd()
            val (fg, bg) = CustomMapAnimator.animateRoute(map.get()!!, routePoints)
            foregroundPolyline = fg
            backgroundPolyline = bg
            CustomMapAnimator.setPrimaryLineColor(Color.parseColor("#000000"))
            CustomMapAnimator.setSecondaryLineColor(Color.parseColor("#ffffff"))
            animateCameraToFillRoute(routePoints)
            setUpMarkerClickListener()
        }
    }

    private var bounds: LatLngBounds.Builder? = LatLngBounds.Builder()

    private fun animateCameraToFillRoute(routePoints: List<LatLng>) {
        bounds = LatLngBounds.Builder()
        CoroutineScope(Dispatchers.Default).launch {
            routePoints.forEach {
                bounds!!.include(it)
            }
            withContext(Dispatchers.Main) {
                map.get()?.setPadding(0, 0, 0, 0)
                if (bounds != null) {
                    map.get()?.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds!!.build(),
                            100
                        )
                    )
                }
            }
        }
    }


    private fun addMarkerToRouteStartAndRouteEnd(
    ) {
        pickUpMarker()
        dropOffMarker()
    }

    private fun dropOffMarker() {

        boxMarker = map.get()?.addMarker(
            MarkerOptions()
                .position(
                    LatLng(
                        googleViewModel.get()!!.dropOffLatitude,
                        googleViewModel.get()!!.dropOffLongitude
                    )
                )
                .icon(BitMapCreator.bitmapDescriptorFromVector(context.get()!!, R.drawable.box))
        )
        createAnnotation(
            googleViewModel.get()?.dropOffLatitude!!,
            googleViewModel.get()?.dropOffLongitude!!,
            Markers.DROP_OFF,
            googleViewModel.get()?.dropOffLocationName?.value.toString()
        )
    }

    private fun pickUpMarker() {
        circularMarker = map.get()?.addMarker(
            MarkerOptions()
                .position(
                    LatLng(
                        googleViewModel.get()!!.pickUpLatitude,
                        googleViewModel.get()!!.pickUpLongitude
                    )
                )
                .icon(BitMapCreator.bitmapDescriptorFromVector(context.get()!!, R.drawable.circle))
        )
        createAnnotation(
            googleViewModel.get()?.pickUpLatitude!!,
            googleViewModel.get()?.pickUpLongitude!!,
            Markers.PICK_UP,
            googleViewModel.get()?.pickUpLocationName?.value.toString()
        )

    }
    fun deleteEveryThingOnMap() {
        pickUpMarker?.remove()
        dropOffMarker?.remove()
        pickUpMarker = null
        dropOffMarker = null
        foregroundPolyline?.remove()
        backgroundPolyline?.remove()
        foregroundPolyline = null
        backgroundPolyline = null
        circularMarker?.remove()
        boxMarker?.remove()
        circularMarker = null
        boxMarker = null
        bounds = null
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


    private fun addAnnotationClickListener(marker: Marker) {
        if (marker.tag != null) {
            if (marker.tag?.equals(pickUpMarker?.tag)!!) {

                sharedViewModel.get()?.setPickUpAnnotationClicked(true)
                sharedViewModel.get()?.setPickUpInputInFocus(true)

            } else if (marker.tag?.equals(dropOffMarker?.tag)!!) {
                sharedViewModel.get()?.setDropOffInputInFocus(true)
                sharedViewModel.get()?.setDropOffAnnotationClicked(true)
            }
            deleteEveryThingOnMap()
        }
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
        val d = SphericalUtil.computeDistanceBetween(p1, p2)
        val h = SphericalUtil.computeHeading(p1, p2)

        val p = SphericalUtil.computeOffset(p1, d * 0.5, h)

        val x = (1 - k * k) * d * 0.5 / (2 * k)
        val r = (1 + k * k) * d * 0.5 / (2 * k)

        val c = SphericalUtil.computeOffset(p, x, h + 90.0)

        val options: PolylineOptions = PolylineOptions()
        val pattern = Arrays.asList(Dash(30f), Gap(20f))

        val h1 = SphericalUtil.computeHeading(c, p1)
        val h2 = SphericalUtil.computeHeading(c, p2)

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

