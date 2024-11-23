package com.example.uber.core.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineResult
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.util.Locale

object FetchLocation {
    private val mCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    private var locationCallback: LocationCallback? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    suspend fun getLocation(latitude: Double, longitude: Double, context: Context): String {
        var addresses: List<Address> = emptyList()
            val geocoder = Geocoder(context, Locale.getDefault())

                try {
                    addresses = geocoder.getFromLocation(
                        latitude,
                        longitude,
                        1
                    )!!

                } catch (e: Exception) {
                    Log.e("getLocation", e.message.toString())
                }
        return if (addresses.isNotEmpty()) addresses[0].getAddressLine(0).split(",")[1] else ""

    }

    fun getCurrentLocation(
        fragmentContext: Fragment,
        context: Context,
        dispatcher: (Location?) -> Unit
    ) {
        lazyInitializeLocationEngine(context)
        mCoroutineScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                val locationRequest = LocationRequest.create().apply {
                    interval = 5000
                    fastestInterval = 2000
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }

                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {

                        val location = locationResult.lastLocation
                        if (location != null) {
                            fusedLocationClient?.removeLocationUpdates(this)
                            dispatcher.invoke(location) // Pass the location to the callback
                        }
                    }
                }
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@launch
                }
                fusedLocationClient?.requestLocationUpdates(
                    locationRequest,
                    locationCallback!!,
                    Looper.getMainLooper()
                )

               fusedLocationClient!!.lastLocation.addOnSuccessListener {
                   runCatching {
                       if (it != null) {
                           dispatcher.invoke(Location("Custom").apply {
                               this.latitude = it.latitude
                               this.longitude = it.longitude
                           })
                       }
                   }
               }.addOnFailureListener { exception ->
                   exception.printStackTrace()

               }
            }catch (it:Exception) {
            }


        }
    }

    private fun lazyInitializeLocationEngine(context: Context) {
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        }
    }



    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    fun cleanResources() {
        fusedLocationClient = null;
    }

    fun customLocationMapper(latitude: Double, longitude: Double): Location {
        val locationMapper = Location("").apply {
            this.latitude = latitude
            this.longitude = longitude
        }
        return locationMapper
    }
}
