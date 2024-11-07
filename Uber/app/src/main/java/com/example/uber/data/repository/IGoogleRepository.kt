package com.example.uber.data.repository

import com.example.uber.data.remote.models.google.geoCodeResponse.GeoCodingGoogleMapsResponse
import retrofit2.Response

interface IGoogleRepository {
    suspend fun geoCodeLocation(latitude: Double, longitude: Double): Response<GeoCodingGoogleMapsResponse>
}