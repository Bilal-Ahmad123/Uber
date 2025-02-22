package com.example.uber.data.local.location.repository

import com.example.uber.data.local.location.entities.Location

interface LocationRepository {
    suspend fun getCurrentLocation(): Location
    suspend fun insertCurrentLocation(location: Location)
    suspend fun deleteCurrentLocation()
}