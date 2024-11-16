package com.example.uber.domain.use_case.geocoding

import com.example.uber.data.local.entities.Location
import com.example.uber.data.repository.ILocationRepository
import javax.inject.Inject

class LocationUseCase @Inject constructor(private val locationRepository: ILocationRepository) {
    suspend fun getCurrentLocation() = locationRepository.getCurrentLocation()
    suspend fun insertCurrentLocation(location: Location) = locationRepository.insertCurrentLocation(location)
    suspend fun deleteCurrentLocation() = locationRepository.deleteCurrentLocation()
}