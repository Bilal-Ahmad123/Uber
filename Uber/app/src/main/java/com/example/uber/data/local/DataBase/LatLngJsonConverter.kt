package com.example.uber.data.local.dataBase
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.mapbox.mapboxsdk.geometry.LatLng

class LatLngJsonConverter {

    @TypeConverter
    fun fromLatLng(latLng: LatLng?): String? {
        return Gson().toJson(latLng)
    }

    @TypeConverter
    fun toLatLng(json: String?): LatLng? {
        return Gson().fromJson(json, LatLng::class.java)
    }
}
