package com.example.uber.core.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.uber.BuildConfig
import com.example.uber.core.interfaces.utils.permissions.Permission
import com.example.uber.core.interfaces.utils.permissions.PermissionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.util.Locale


object FetchLocation {
    private val mCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    private val navigationLocationProvider = NavigationLocationProvider()
    private var permissionManager: PermissionManager? = null;
    private lateinit var fusedLocationClient: FusedLocationProviderClient

     fun getCurrentLocation(context: Context) {
         fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
         fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Log.d("Location", "Current location: Latitude = $latitude, Longitude = $longitude")
                } else {
                    Log.e("Location", "Location not available")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Location", "Failed to get location: ${e.message}")
            }
    }


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
//
//    fun getCurrentLocation(
//        fragmentContext: Fragment,
//        context: Context,
//        dispatcher: (Location?) -> Unit
//    ) {
//        lazyInitializeLocationEngine(context)
//        lazyInitializePermissionManager(fragmentContext)
//        mCoroutineScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
//            Log.d("mCoroutineScope", "Running")
//            val lastLocation: Unit = locationEngine!!.getLastLocation(object :
//                LocationEngineCallback<LocationEngineResult> {
//                override fun onSuccess(result: LocationEngineResult?) {
//                    dispatcher.invoke(result?.locations?.get(0))
//                }
//
//                override fun onFailure(exception: Exception) {
//                    Toast.makeText(context, exception.message, Toast.LENGTH_SHORT)
//                        .show()
//                }
//
//            })
//
//
//        }
//    }
//
//    private fun lazyInitializeLocationEngine(context: Context) {
//        if (locationEngine == null) {
//            locationEngine = LocationEngineProvider.getBestLocationEngine(context)
//        }
//    }
//
//
//    private fun lazyInitializePermissionManager(context: Fragment) {
//        if (permissionManager == null) {
//            permissionManager = PermissionManager.from(context)
//        }
//    }
//
//    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
//        throwable.printStackTrace()
//    }
//
//    fun cleanResources() {
//        locationEngine = null;
//        permissionManager = null;
//    }
//
//    fun customLocationMapper(latitude: Double, longitude: Double): Location {
//        val locationMapper = Location("").apply {
//            this.latitude = latitude
//            this.longitude = longitude
//        }
//        return locationMapper
//    }
}
