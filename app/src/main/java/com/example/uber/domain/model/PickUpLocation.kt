package com.example.uber.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pickUpLocationTable")
data class PickUpLocation(
    @PrimaryKey val locationId: Int = 0,
    val pickUpLocation:String,
)