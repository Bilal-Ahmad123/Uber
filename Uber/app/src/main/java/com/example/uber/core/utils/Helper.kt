package com.example.uber.core.utils

import com.example.uber.presentation.riderpresentation.map.RouteCreationHelper
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

object Helper {
     fun calculateBounds(latLngBounds:List<LatLng>?): LatLngBounds? {
        val builder = LatLngBounds.Builder()
        val latLngList = latLngBounds

        if (latLngList == null) {
            return null
        }

        latLngList?.forEach {
            builder.include(it)
        }

        return builder.build()
    }
}