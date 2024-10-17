package com.example.uber.data.repository

import com.example.uber.data.remote.GeoCode.MapBox.GeoCodingResponse
import retrofit2.Response

interface IGetGeoCodeLocationRepository {
    suspend fun geoCodeLocation(latitude:Double,longitude:Double): Response<GeoCodingResponse>
}