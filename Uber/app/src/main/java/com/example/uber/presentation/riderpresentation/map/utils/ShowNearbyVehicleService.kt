package com.example.uber.presentation.riderpresentation.map.utils

import android.content.Context
import android.location.Location
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import com.example.uber.R
import com.example.uber.core.utils.BitMapCreator
import com.example.uber.core.utils.HRMarkerAnimation
import com.example.uber.domain.local.location.model.DriverLocationMarker
import com.example.uber.presentation.riderpresentation.viewModels.SocketViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.UUID

class ShowNearbyVehicleService(
    private val googleMap: WeakReference<GoogleMap>,
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val viewLifecycleOwner: LifecycleOwner,
    private val context: WeakReference<Context>,
) {
    private val drivers = mutableMapOf<UUID, DriverLocationMarker>()

    private val socketViewModel: SocketViewModel by lazy {
        ViewModelProvider(viewModelStoreOwner)[SocketViewModel::class.java]
    }

    fun startObservingNearbyVehicles() {
        viewLifecycleOwner.lifecycleScope.launch {
            with(socketViewModel) {
                driverLocation.collectLatest {
                    if (!drivers.containsKey(it.driverId)) {
                        val marker = googleMap.get()?.addMarker(
                            MarkerOptions().position(LatLng(it.latitude, it.longitude)).visible(false)
                                .icon(
                                    BitMapCreator.bitmapDescriptorFromVector(
                                        R.drawable.ic_car,
                                        context.get()!!
                                    )
                                )
                        )
                        drivers[it.driverId] = DriverLocationMarker(
                            latLngToLocation(
                                LatLng(
                                    it.latitude,
                                    it.longitude
                                )
                            ), latLngToLocation(LatLng(it.latitude, it.longitude)), marker!!,
                            HRMarkerAnimation(
                                googleMap.get(), 1000
                            ) {},
                            it.vehicleType
                        )
                    } else {
                        val driverLocationObj = drivers[it.driverId]
                        driverLocationObj?.mLastLocation =
                            latLngToLocation(LatLng(it.latitude, it.longitude))
                        animateMarker(driverLocationObj!!)
                    }
                }
            }
        }
    }

    private fun latLngToLocation(latLng: LatLng): Location {
        val location = Location("provider")
        location.latitude = latLng.latitude
        location.longitude = latLng.longitude
        return location
    }

    private fun animateMarker(driverLocationObj: DriverLocationMarker) {
        driverLocationObj.hrMarker.animateMarker(
            driverLocationObj.mLastLocation,
            driverLocationObj.oldLocation,
            driverLocationObj.driverMarker
        )
    }

    fun onCarItemListClickListener(){

    }
}