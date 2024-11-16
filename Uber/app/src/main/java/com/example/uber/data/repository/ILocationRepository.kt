package com.example.uber.data.repository

import com.example.uber.data.local.entities.Location

interface ILocationRepository {
    suspend fun getCurrentLocation(): Location
    suspend fun insertCurrentLocation(location: Location)
    suspend fun deleteCurrentLocation()
}