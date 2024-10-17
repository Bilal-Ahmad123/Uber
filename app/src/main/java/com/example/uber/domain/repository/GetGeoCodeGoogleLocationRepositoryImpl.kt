package com.example.uber.domain.repository

import com.example.uber.data.remote.GeoCode.GoogleMaps.GeoCodingGoogleMapsResponse
import com.example.uber.data.remote.GeoCode.GoogleMaps.IGeocodingGoogleMapService
import com.example.uber.data.remote.GeoCode.MapBox.GeoCodingResponse
import com.example.uber.data.repository.IGetGeoCodeGoogleLocationRepository
import retrofit2.Response
import javax.inject.Inject

class GetGeoCodeGoogleLocationRepositoryImpl @Inject constructor(private val googleApi: IGeocodingGoogleMapService): IGetGeoCodeGoogleLocationRepository {
    override suspend fun geoCodeLocation(latitude:Double,longitude:Double): Response<GeoCodingGoogleMapsResponse> {
        return googleApi.geoCodeLocation("${latitude},${longitude}")
    }
}