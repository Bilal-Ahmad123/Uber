package com.example.uber.core.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

object FetchLocation {
    suspend fun getLocation(latitude:Double, longitude:Double, context: Context):String{
        val geocoder = Geocoder(context, Locale.getDefault())
        var addresses: List<Address> = emptyList()

        withContext(Dispatchers.IO) {
            try {
                addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
                )!!

            }
            catch(e:Exception){
                Log.e("getLocation", e.message.toString())
            }
        }
        return if (addresses.isNotEmpty()) addresses[0].getAddressLine(0).split(",")[0] else ""

    }
}