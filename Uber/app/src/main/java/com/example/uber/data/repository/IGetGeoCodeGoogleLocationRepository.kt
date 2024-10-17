package com.example.uber.data.repository

import com.example.uber.data.remote.GeoCode.GoogleMaps.GeoCodingGoogleMapsResponse
import retrofit2.Response

interface IGetGeoCodeGoogleLocationRepository {
    suspend fun geoCodeLocation(latitude: Double, longitude: Double): Response<GeoCodingGoogleMapsResponse>
}