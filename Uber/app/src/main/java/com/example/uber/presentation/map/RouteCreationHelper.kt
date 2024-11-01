package com.example.uber.presentation.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.uber.BuildConfig
import com.example.uber.R
import com.example.uber.presentation.viewModels.DropOffLocationViewModel
import com.example.uber.presentation.viewModels.PickUpLocationViewModel
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
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

class RouteCreationHelper(
    private val mapView: WeakReference<MapView>,
    private val map: WeakReference<MapboxMap>,
    private val context: Context,
    private var pickUpLocationViewModel: PickUpLocationViewModel,
    private var dropOffLocationViewModel: DropOffLocationViewModel
) {
    private val mCouroutineScope = CoroutineScope(Dispatchers.IO)

    fun createRoute(pickUpLocation: Point, dropOffLocation: Point): RouteCreationHelper {
        mCouroutineScope.launch {
            val originPoint =
                Point.fromLngLat(pickUpLocation.longitude(), pickUpLocation.latitude())
            val destinationPoint =
                Point.fromLngLat(dropOffLocation.longitude(), dropOffLocation.latitude())
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
                        if (routes != null && routes.isNotEmpty()) {
                            val route = routes[0]
                            mCouroutineScope.launch {
                                displayRoute(
                                    route,
                                    pickUpLocation.latitude(),
                                    pickUpLocation.longitude(),
                                    dropOffLocation.latitude(),
                                    dropOffLocation.longitude()
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
        return this


    }


    private suspend fun displayRoute(
        route: DirectionsRoute,
        pickUpLatitude: Double,
        pickUpLongitude: Double,
        dropOffLatitude: Double,
        dropOffLongitude: Double
    ) {
        val lineString = LineString.fromPolyline(route.geometry()!!, 6)

        withContext(Dispatchers.Main) {
            val lineManager = LineManager(mapView.get()!!, map.get()!!, map.get()!!.style!!)


            addMarkerIconsToStyle(map.get()!!.style!!)
            addMarkerAnnotationToStyle(map.get()!!.style!!)
            val lineOptions = LineOptions()
                .withLineColor(Color.parseColor("#3887BE").toString())
                .withLineWidth(3f)
            lineManager.create(lineOptions.withGeometry(lineString))
            addMarkerToRouteStartAndRouteEnd(
                pickUpLatitude,
                pickUpLongitude,
                dropOffLatitude,
                dropOffLongitude
            )
            addMarkerAnnotation(pickUpLatitude, pickUpLongitude, dropOffLatitude, dropOffLongitude)
        }
    }

    private fun addMarkerToRouteStartAndRouteEnd(
        pickUpLatitude: Double,
        pickUpLongitude: Double,
        dropOffLatitude: Double,
        dropOffLongitude: Double
    ) {
        var symbolManager = SymbolManager(mapView.get()!!, map.get()!!, map.get()!!.style!!)


        val symbolOptionsPickUp = SymbolOptions()
            .withLatLng(LatLng(pickUpLatitude, pickUpLongitude))
            .withIconImage("pickup-marker")
            .withIconSize(1.3f)
        symbolManager.create(symbolOptionsPickUp)

        val symbolOptionsDropOff = SymbolOptions()
            .withLatLng(LatLng(dropOffLatitude, dropOffLongitude))
            .withIconImage("dropoff-marker")
            .withIconSize(1.3f)
        symbolManager.create(symbolOptionsDropOff)
    }

    private fun addMarkerAnnotation(
        pickUpLatitude: Double,
        pickUpLongitude: Double,
        dropOffLatitude: Double,
        dropOffLongitude: Double
    ) {
        var symbolManager = SymbolManager(mapView.get()!!, map.get()!!, map.get()!!.style!!)
        val symbolOptionsPickUpAnnotation = SymbolOptions()
            .withLatLng(LatLng(pickUpLatitude, pickUpLongitude))
            .withIconImage("pickup-marker-annotation")
            .withIconOffset(arrayOf(0f, -30f))

        symbolManager.create(symbolOptionsPickUpAnnotation)

        val symbolOptionsDropOffAnnotation = SymbolOptions()
            .withLatLng(LatLng(dropOffLatitude, dropOffLongitude))
            .withIconImage("dropoff-marker-annotation")
            .withIconOffset(arrayOf(0f, -30f))
        symbolManager.create(symbolOptionsDropOffAnnotation)
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

        val pickupMarker = createTextMarkerDrawable(context, pickUpLocationViewModel.locationName.value.toString())
        val dropoffMarker = createTextMarkerDrawable(context, dropOffLocationViewModel.locationName.value.toString())
        style.addImage("pickup-marker-annotation", pickupMarker)
        style.addImage("dropoff-marker-annotation", dropoffMarker)

    }

    private fun scaleBitMapImageSize(icon: Bitmap): Bitmap? {
        val scaledIcon = icon?.let {
            Bitmap.createScaledBitmap(it, 20, 20, false)
        }
        return scaledIcon
    }
    private fun createTextMarkerDrawable(context: Context, text: String): Bitmap {
        val shapeDrawable = ContextCompat.getDrawable(context, R.drawable.marker_annotations)!!
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 20f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        val rect = Rect();
        paint.getTextBounds(text, 0, text.length, rect)
        val height = shapeDrawable.intrinsicHeight
        shapeDrawable.setBounds(0, 0, rect.width() + 20, height)

        val bitmap = Bitmap.createBitmap(rect.width() + 20, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        shapeDrawable.draw(canvas)

        val xPos = canvas.width / 2f
        val yPos = (canvas.height / 2f) - (paint.descent() + paint.ascent()) / 2
        canvas.drawText(text, xPos, yPos, paint)

        return bitmap
    }

    private fun getDurationInMinutes(seconds: Int):Int{
        val minutes = (seconds % 3600) / 60
        return minutes
    }

}

