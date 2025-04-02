package com.example.uber.domain.local.location.model

import android.location.Location
import com.example.uber.core.utils.HRMarkerAnimation
import com.example.uber.domain.remote.general.model.response.NearbyVehicles
import com.google.android.gms.maps.model.Marker

data class DriverLocationMarker(var mLastLocation: Location, var oldLocation: Location, val driverMarker: Marker,val hrMarker: HRMarkerAnimation, var vehicleType : String)