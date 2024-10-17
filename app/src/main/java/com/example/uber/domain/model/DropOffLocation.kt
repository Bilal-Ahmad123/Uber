package com.example.uber.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dropOffLocationTable")
data class DropOffLocation(
    @PrimaryKey val locationId: Int = 0,
    val dropOffLocation: String,
)
