package com.example.uber.data.local.location.dataBase
import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson

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
