//package com.example.uber.presentation.map
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.Color
//import android.util.Log
//import android.view.View
//import android.widget.FrameLayout
//import android.widget.LinearLayout
//import android.widget.TextView
//import androidx.core.content.ContextCompat
//import com.example.uber.BuildConfig
//import com.example.uber.R
//import com.mapbox.api.directions.v5.DirectionsCriteria
//import com.mapbox.api.directions.v5.MapboxDirections
//import com.mapbox.api.directions.v5.models.DirectionsResponse
//import com.mapbox.api.directions.v5.models.DirectionsRoute
//import com.mapbox.geojson.LineString
//import com.mapbox.geojson.Point
//import com.mapbox.maps.MapView
//import com.mapbox.maps.MapboxMap
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.lang.ref.WeakReference
//
//class RouteCreationHelper(
//    private val mapView: WeakReference<MapView>,
//    private val map: WeakReference<MapboxMap>,
//    private val context:Context
//) {
//    private val mCouroutineScope = CoroutineScope(Dispatchers.IO)
//
//    fun createRoute(pickUpLocation: Point, dropOffLocation: Point) {
//        mCouroutineScope.launch {
//            val originPoint =
//                Point.fromLngLat(pickUpLocation.longitude(), pickUpLocation.latitude())
//            val destinationPoint =
//                Point.fromLngLat(dropOffLocation.longitude(), dropOffLocation.latitude())
//            val directionsCall = MapboxDirections.builder()
//                .origin(originPoint)
//                .destination(destinationPoint)
//                .overview(DirectionsCriteria.OVERVIEW_FULL)
//                .profile(DirectionsCriteria.PROFILE_DRIVING)
//                .accessToken(BuildConfig.MAPBOX_TOKEN)
//                .build()
//
//            directionsCall.enqueueCall(object : Callback<DirectionsResponse> {
//                override fun onResponse(
//                    call: Call<DirectionsResponse>,
//                    response: Response<DirectionsResponse>
//                ) {
//                    if (response.isSuccessful) {
//                        val routes: MutableList<DirectionsRoute>? = response.body()?.routes()
//                        if (routes != null && routes.isNotEmpty()) {
//                            val route = routes[0]
//                            mCouroutineScope.launch {
//                                displayRoute(
//                                    route,
//                                    pickUpLocation.latitude(),
//                                    pickUpLocation.longitude(),
//                                    dropOffLocation.latitude(),
//                                    dropOffLocation.longitude()
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
//                    TODO("Not yet implemented")
//                }
//
//            })
//
//        }
//
//
//    }
//
//
//    private suspend fun displayRoute(
//        route: DirectionsRoute,
//        pickUpLatitude: Double,
//        pickUpLongitude: Double,
//        dropOffLatitude: Double,
//        dropOffLongitude: Double
//    ) {
//        val lineString = LineString.fromPolyline(route.geometry()!!, 6)
//
//        withContext(Dispatchers.Main) {
//            val lineManager = LineManager(mapView.get()!!, map.get()!!, map.get()!!.style!!)
//
//
////            addMarkerIconsToStyle(map.get()!!.style!!)
////            createAnnotation(pickUpLatitude,pickUpLongitude)
//            val lineOptions = LineOptions()
//                .withLineColor(Color.parseColor("#3887BE").toString())
//                .withLineWidth(3f)
//            lineManager.create(lineOptions.withGeometry(lineString))
//            addMarkerToRouteStartAndRouteEnd(
//                pickUpLatitude,
//                pickUpLongitude,
//                dropOffLatitude,
//                dropOffLongitude
//            )
//        }
//    }
//
//    private fun addMarkerToRouteStartAndRouteEnd(
//        pickUpLatitude: Double,
//        pickUpLongitude: Double,
//        dropOffLatitude: Double,
//        dropOffLongitude: Double
//    ) {
//        var symbolManager = SymbolManager(mapView.get()!!, map.get()!!, map.get()!!.style!!)
//
//
//        val symbolOptionsPickUp = SymbolOptions()
//            .withLatLng(LatLng(pickUpLatitude, pickUpLongitude))
//            .withIconImage("pickup-marker")
//            .withIconSize(1.3f)
//        symbolManager.create(symbolOptionsPickUp)
//
//        val symbolOptionsDropOff = SymbolOptions()
//            .withLatLng(LatLng(dropOffLatitude, dropOffLongitude))
//            .withIconImage("dropoff-marker")
//            .withIconSize(1.3f)
//        symbolManager.create(symbolOptionsDropOff)
//        addCustomViewsToPickUpAndDropOff(pickUpLatitude, pickUpLongitude, dropOffLatitude, dropOffLongitude)
//    }
//
//
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
//
//    private fun scaleBitMapImageSize(icon: Bitmap): Bitmap? {
//        val scaledIcon = icon?.let {
//            Bitmap.createScaledBitmap(it, 20, 20, false)
//        }
//        return scaledIcon
//    }
//
//
//    private fun addCustomViewsToPickUpAndDropOff(
//        pickUpLatitude: Double,
//        pickUpLongitude: Double,
//        dropOffLatitude: Double,
//        dropOffLongitude: Double
//    ) {
//        val mapboxMap = map.get()
//        val mapViewLayout = mapView.get()
//
//        if (mapboxMap != null && mapViewLayout != null) {
//            // Create overlay for custom views
//            val overlay = FrameLayout(mapViewLayout.context)
//            overlay.layoutParams = FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.MATCH_PARENT,
//                FrameLayout.LayoutParams.MATCH_PARENT
//            )
//
//            // Create custom view for pickup
//            val pickupView = LinearLayout(mapViewLayout.context).apply {
//                setBackgroundColor(Color.BLACK)
//                layoutParams = FrameLayout.LayoutParams(100, 100)
//                orientation = LinearLayout.VERTICAL
//            }
//
//            // Create TextView for pickup
//            val pickupTextView = TextView(mapViewLayout.context).apply {
//                text = "Pickup Location"
//                setTextColor(Color.WHITE)
//                textSize = 14f
//            }
//
//            // Add TextView to pickup view
//            pickupView.addView(pickupTextView)
//
//            // Create custom view for dropoff
//            val dropOffView = LinearLayout(mapViewLayout.context).apply {
//                setBackgroundColor(Color.BLACK)
//                layoutParams = FrameLayout.LayoutParams(100, 100)
//                orientation = LinearLayout.VERTICAL
//            }
//
//            // Create TextView for dropoff
//            val dropoffTextView = TextView(mapViewLayout.context).apply {
//                text = "Dropoff Location"
//                setTextColor(Color.WHITE)
//                textSize = 14f
//            }
//
//            // Add TextView to dropoff view
//            dropOffView.addView(dropoffTextView)
//
//            // Convert geo to screen coordinates
//            val pickupPoint = mapboxMap.projection.toScreenLocation(LatLng(pickUpLatitude, pickUpLongitude))
//            val dropoffPoint = mapboxMap.projection.toScreenLocation(LatLng(dropOffLatitude, dropOffLongitude))
//
//            // Set layout parameters for pickup view
//            val pickupLayoutParams = FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.WRAP_CONTENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                leftMargin = pickupPoint.x.toInt()
//                topMargin = pickupPoint.y.toInt()
//            }
//
//            // Set layout parameters for dropoff view
//            val dropoffLayoutParams = FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.WRAP_CONTENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                leftMargin = dropoffPoint.x.toInt()
//                topMargin = dropoffPoint.y.toInt()
//            }
//
//            // Add views to overlay
//            overlay.addView(pickupView, pickupLayoutParams)
//            overlay.addView(dropOffView, dropoffLayoutParams)
//
//            // Add overlay to mapView
//            mapViewLayout.addView(overlay)
//        }
//    }
//
////    private fun createAnnotation(pickUpLatitude: Double, pickUpLongitude: Double){
////        val icon = IconFactory.getInstance(context)
////        icon.fromResource(R.drawable.baseline_home_24)
////
////        map.get()?.addMarker(MarkerOptions()
////            .position(LatLng(-37.821648, 144.978594))
////            .title("Nice"))
////    }
//
//
//
//
//}