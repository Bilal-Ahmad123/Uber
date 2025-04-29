package com.example.uber.presentation.riderpresentation.map.Routes

import android.graphics.Color
import android.location.Location
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.uber.core.utils.HRMarkerAnimation
import com.example.uber.presentation.riderpresentation.map.viewmodels.RideViewModel
import com.example.uber.presentation.riderpresentation.viewModels.GoogleViewModel
import com.example.uber.presentation.riderpresentation.viewModels.LocationViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class TripRoute(
    private val googleMap: WeakReference<GoogleMap>,
    private val googleViewModel: GoogleViewModel,
    private val viewLifecycleOwner: LifecycleOwner,
    private val locationViewModel: LocationViewModel,
    private val rideViewModel: RideViewModel
) {
    private var driverMarker: Marker? = null
    private var oldLocation: Location? = null
    private var mLastLocation: Location? = null
    fun createRoute(
        pickUpLocation: LatLng,
        dropOffLocation: LatLng,
    ) {
        googleViewModel.directionsRequest(pickUpLocation, dropOffLocation)
        observeDirectionsResponse()
        observeTripUpdates()
        startObservingTripUpdates()
    }

    private fun observeDirectionsResponse() {
        googleViewModel?.run {
            directions.observe(viewLifecycleOwner) {
                Log.d("observeDirectionsResponse", "observeDirectionsResponse: ${it.data}")
                if (it.data!!.routes.isNotEmpty()) {

                    createRoute(it.data!!.routes[0].overview_polyline!!.points)
                }
            }
        }
    }

    private fun createRoute(line: String) {
        val routePoints: List<LatLng> = PolyUtil.decode(line)
        if (routePoints.size > 1) {
            val polylineOptions = PolylineOptions()
                .width(5f)
                .color(Color.BLUE)

            polylineOptions.addAll(routePoints)
            googleMap.get()?.addPolyline(polylineOptions)
            addMarker()
        }
    }

    private fun addMarker() {
        driverMarker = googleMap.get()?.addMarker(
            MarkerOptions().position(LatLng(33.591293, 73.122300)))
    }

    private fun animateMarker() {
        HRMarkerAnimation(
            googleMap.get(), 1000
        ) { updatedLocation -> oldLocation = updatedLocation }.animateMarker(
            mLastLocation,
            oldLocation,
            driverMarker
        )
    }


    private var tripJob : Job? = null
    private fun observeTripUpdates(){
        tripJob = viewLifecycleOwner.lifecycleScope.launch {
            rideViewModel.apply {
                tripUpdates.collectLatest { a->
                    mLastLocation = Location("").apply {
                        latitude = a.latitude
                        longitude = a.longitude
                    }
                    animateMarker()
                }
            }
        }
    }

    private fun startObservingTripUpdates(){
        viewLifecycleOwner.lifecycleScope.launch {
            rideViewModel.observeTripLocation()
        }
    }


}