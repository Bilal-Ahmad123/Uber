package com.example.uber.domain.repository

import com.example.uber.data.remote.GeoCode.MapBox.GeoCodingResponse
import com.example.uber.data.remote.GeoCode.MapBox.IGeocodingService
import com.example.uber.data.repository.IGetGeoCodeLocationRepository
import retrofit2.Response
import javax.inject.Inject

class GetGeoCodeLocationRepositoryImpl @Inject constructor(private val mapBoxApi: IGeocodingService) : IGetGeoCodeLocationRepository {
    override suspend fun geoCodeLocation(latitude:Double,longitude:Double): Response<GeoCodingResponse> {
        return mapBoxApi.geoCodeLocation(latitude,longitude)
    }

}