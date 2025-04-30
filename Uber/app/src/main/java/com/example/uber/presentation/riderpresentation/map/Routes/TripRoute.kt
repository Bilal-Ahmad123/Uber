package com.example.uber.presentation.riderpresentation.map.Routes

import android.graphics.Color
import android.location.Location
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.uber.core.utils.HRMarkerAnimation
import com.example.uber.data.remote.api.backend.rider.socket.ride.model.TripLocation
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
    private var riderPickUpLocation: LatLng? = null
    fun createRoute(
        pickUpLocation: LatLng,
        driverInitialLocation: LatLng,
    ) {
        riderPickUpLocation = pickUpLocation
        googleViewModel.directionsRequest(pickUpLocation, driverInitialLocation)
        observeDirectionsResponse()
        observeTripUpdates()
        startObservingTripUpdates()
        addMarker(driverInitialLocation)
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

    private var polyLine: List<LatLng>? = null

    private fun createRoute(line: String) {
        val routePoints: List<LatLng> = PolyUtil.decode(line)
        if (routePoints.size > 1) {
            polyLine = routePoints
            val polylineOptions = PolylineOptions()
                .width(5f)
                .color(Color.BLUE)

            polylineOptions.addAll(routePoints)
            googleMap.get()?.addPolyline(polylineOptions)
        }
    }

    private fun addMarker(driverInitialLocation: LatLng) {
        driverMarker = googleMap.get()?.addMarker(
            MarkerOptions().position(
                LatLng(
                    driverInitialLocation.latitude,
                    driverInitialLocation.longitude
                )
            )
        )
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


    private var tripJob: Job? = null
    private fun observeTripUpdates() {
        tripJob = viewLifecycleOwner.lifecycleScope.launch {
            rideViewModel.apply {
                tripUpdates.collectLatest { a ->
                    mLastLocation = Location("").apply {
                        latitude = a.latitude
                        longitude = a.longitude
                    }
                    animateMarker()
                    checkIfDriverLocationOnRoute(a)
                    removeTravelledPolyLine()
                }
            }
        }
    }

    private fun startObservingTripUpdates() {
        viewLifecycleOwner.lifecycleScope.launch {
            rideViewModel.observeTripLocation()
        }
    }

    private fun checkIfDriverLocationOnRoute(trip: TripLocation) {
        if (!PolyUtil.isLocationOnPath(
                LatLng(trip.latitude, trip.longitude),
                polyLine,
                true,
                50.0
            )
        ) {
            googleViewModel.directionsRequest(
                riderPickUpLocation!!,
                LatLng(trip.latitude, trip.longitude)
            )
        }
    }

    private fun removeTravelledPolyLine() {
        var currentIndexOnPolyLine: Int? =
            polyLine?.indexOfFirst { it.latitude == mLastLocation?.latitude && it.longitude == mLastLocation?.longitude }
        currentIndexOnPolyLine?.let {
            polyLine?.subList(it, polyLine!!.size)
        }
    }


}