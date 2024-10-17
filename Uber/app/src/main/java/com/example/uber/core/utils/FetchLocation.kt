package com.example.uber.core.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.uber.core.interfaces.utils.permissions.Permission
import com.example.uber.core.interfaces.utils.permissions.PermissionManager
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
    private var locationEngine: LocationEngine? = null;
    private var permissionManager: PermissionManager? = null;
    suspend fun getLocation(latitude: Double, longitude: Double, context: Context): String {
        var addresses: List<Address> = emptyList()
        mCoroutineScope.launch(Dispatchers.IO) {
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

        }
        return if (addresses.isNotEmpty()) addresses[0].getAddressLine(0).split(",")[0] else ""

    }

    fun getCurrentLocation(
        fragmentContext: Fragment,
        context: Context,
        dispatcher: (Location?) -> Unit
    ) {
        lazyInitializeLocationEngine(context)
        lazyInitializePermissionManager(fragmentContext)
        mCoroutineScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            Log.d("mCoroutineScope", "Running")
            val lastLocation: Unit = locationEngine!!.getLastLocation(object :
                LocationEngineCallback<LocationEngineResult> {
                override fun onSuccess(result: LocationEngineResult?) {
                    dispatcher.invoke(result?.locations?.get(0))
                }

                override fun onFailure(exception: Exception) {
                    Toast.makeText(context, exception.message, Toast.LENGTH_SHORT)
                        .show()
                }

            })


        }
    }

    private fun lazyInitializeLocationEngine(context: Context) {
        if (locationEngine == null) {
            locationEngine = LocationEngineProvider.getBestLocationEngine(context)
        }
    }


    private fun lazyInitializePermissionManager(context: Fragment) {
        if (permissionManager == null) {
            permissionManager = PermissionManager.from(context)
        }
    }

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    fun cleanResources() {
        locationEngine = null;
        permissionManager = null;
    }

    fun customLocationMapper(latitude: Double, longitude: Double): Location {
        val locationMapper = Location("").apply {
            this.latitude = latitude
            this.longitude = longitude
        }
        return locationMapper
    }
}
