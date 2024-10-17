package com.example.uber.domain.repository

import com.example.uber.data.local.Dao.PickUpLocationDao
import com.example.uber.data.remote.GeoCode.GoogleMaps.GeoCodingGoogleMapsResponse
import com.example.uber.data.remote.GeoCode.GoogleMaps.IGeocodingGoogleMapService
import com.example.uber.data.repository.IPickUpLocationRepository
import com.example.uber.domain.model.PickUpLocation
import retrofit2.Response
import javax.inject.Inject

class PickUpLocationRepositoryImpl @Inject constructor(private val pickUpLocationDao: PickUpLocationDao, private val googleApi: IGeocodingGoogleMapService) :
    IPickUpLocationRepository {
    override suspend fun getPickUpLocation() = pickUpLocationDao.getPickUpLocation()
    override suspend fun insertPickUpLocation(pickUpLocation: PickUpLocation) =
        pickUpLocationDao.insertPickUpLocation(pickUpLocation)

    override suspend fun deletePickUpLocation() = pickUpLocationDao.deletePickUpLocation()
    override suspend fun getLocationFromGoogleApi(
        latitude: Double,
        longitude: Double
    ): Response<GeoCodingGoogleMapsResponse> {
        return googleApi.geoCodeLocation("${latitude},${longitude}")
    }

}