package com.example.uber.domain.local.location.repository

import com.example.uber.data.local.location.dao.LocationDao
import com.example.uber.data.local.location.entities.Location
import com.example.uber.data.local.location.repository.LocationRepository
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(private val locationDao: LocationDao):
    LocationRepository {
    override suspend fun getCurrentLocation() = locationDao.getCurrentLocation()
    override suspend fun insertCurrentLocation(location: Location) = locationDao.insertCurrentLocation(location)
    override suspend fun deleteCurrentLocation() = locationDao.deleteCurrentLocation()
}