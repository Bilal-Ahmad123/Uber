package com.example.uber.domain.repository

import com.example.uber.data.local.Dao.DropOffLocationDao
import com.example.uber.data.remote.api.GoogleMaps.IGoogleMapService
import com.example.uber.data.remote.models.google.geoCodeResponse.GeoCodingGoogleMapsResponse
import com.example.uber.data.repository.IDropOffLocationRepository
import com.example.uber.domain.model.DropOffLocation
import retrofit2.Response
import javax.inject.Inject

class DropOffLocationRepositoryImpl @Inject constructor(private val dropOffLocationDao: DropOffLocationDao, private val googleApi: IGoogleMapService):IDropOffLocationRepository {
    override suspend fun getDropOffLocation() = dropOffLocationDao.getDropOffLocation()
    override suspend fun insertDropOffLocation(dropOffLocation: DropOffLocation) =
        dropOffLocationDao.insertDropOffLocation(dropOffLocation)

    override suspend fun deleteDropOffLocation() = dropOffLocationDao.deleteDropOffLocation()
    override suspend fun getLocationFromGoogleApi(
        latitude: Double,
        longitude: Double
    ): Response<GeoCodingGoogleMapsResponse> {
        return googleApi.geoCodeLocation("${latitude},${longitude}")
    }
}