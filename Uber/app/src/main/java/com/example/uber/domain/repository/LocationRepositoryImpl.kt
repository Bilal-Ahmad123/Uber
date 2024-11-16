package com.example.uber.domain.repository

import com.example.uber.data.local.dao.LocationDao
import com.example.uber.data.local.entities.Location
import com.example.uber.data.repository.ILocationRepository
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(private val locationDao:LocationDao):ILocationRepository {
    override suspend fun getCurrentLocation() = locationDao.getCurrentLocation()
    override suspend fun insertCurrentLocation(location: Location) = locationDao.insertCurrentLocation(location)
    override suspend fun deleteCurrentLocation() = locationDao.deleteCurrentLocation()
}