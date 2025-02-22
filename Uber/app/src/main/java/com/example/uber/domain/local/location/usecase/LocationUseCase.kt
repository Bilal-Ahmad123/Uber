package com.example.uber.domain.local.location.usecase

import com.example.uber.data.local.location.entities.Location
import com.example.uber.data.local.location.repository.LocationRepository
import javax.inject.Inject

class LocationUseCase @Inject constructor(private val locationRepository: LocationRepository) {
    suspend fun getCurrentLocation() = locationRepository.getCurrentLocation()
    suspend fun insertCurrentLocation(location: Location) = locationRepository.insertCurrentLocation(location)
    suspend fun deleteCurrentLocation() = locationRepository.deleteCurrentLocation()
}