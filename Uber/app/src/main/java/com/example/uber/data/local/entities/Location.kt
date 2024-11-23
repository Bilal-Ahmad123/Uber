package com.example.uber.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "locationTable")
data class Location(
    @PrimaryKey val locationId: Int = 0,
    val location: LatLng,
)