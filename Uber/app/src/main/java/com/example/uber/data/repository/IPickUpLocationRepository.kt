package com.example.uber.data.repository

import com.example.uber.data.remote.models.google.geoCodeResponse.GeoCodingGoogleMapsResponse
import com.example.uber.domain.model.PickUpLocation
import retrofit2.Response

interface IPickUpLocationRepository {
    suspend fun getPickUpLocation(): PickUpLocation
    suspend fun insertPickUpLocation(pickUpLocation: PickUpLocation)
    suspend fun deletePickUpLocation()
    suspend fun getLocationFromGoogleApi(latitude: Double, longitude: Double): Response<GeoCodingGoogleMapsResponse>
}