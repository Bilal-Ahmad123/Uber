package com.example.uber.presentation.map

import android.graphics.Color
import android.util.Log
import com.example.uber.BuildConfig
import com.example.uber.R
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.annotation.LineManager
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RouteCreationHelper(private val mapView: MapView,private val map:MapboxMap) {
    private val mCouroutineScope = CoroutineScope(Dispatchers.IO)
      fun createRoute(pickUpLocation: Point, dropOffLocation: Point) {
         mCouroutineScope.launch {
             val originPoint =
                 Point.fromLngLat(pickUpLocation.longitude(), pickUpLocation.latitude())
             val destinationPoint =
                 Point.fromLngLat(dropOffLocation.longitude(), dropOffLocation.latitude())
             val directionsCall = MapboxDirections.builder()
                 .origin(originPoint)
                 .destination(destinationPoint)
                 .overview(DirectionsCriteria.OVERVIEW_FULL)
                 .profile(DirectionsCriteria.PROFILE_DRIVING) // You can choose other profiles like walking or cycling
                 .accessToken(BuildConfig.MAPBOX_TOKEN) // Replace with your access token
                 .build()

             directionsCall.enqueueCall(object : Callback<DirectionsResponse> {
                 override fun onResponse(
                     call: Call<DirectionsResponse>,
                     response: Response<DirectionsResponse>
                 ) {
                     if (response.isSuccessful) {
                         val routes: MutableList<DirectionsRoute>? = response.body()?.routes()
                         if (routes != null && routes.isNotEmpty()) {
                             val route = routes[0]
                             mCouroutineScope.launch {
                                 displayRoute(route)
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


    }


    private suspend fun displayRoute(route: DirectionsRoute) {
        val lineString = LineString.fromPolyline(route.geometry()!!, 6)

        withContext(Dispatchers.Main) {
            val lineManager = LineManager(mapView, map, map.style!!)


            val lineOptions = LineOptions()
                .withLineColor(Color.parseColor("#3887BE").toString())
                .withLineWidth(3f)
            lineManager.create(lineOptions.withGeometry(lineString))
        }
    }
}