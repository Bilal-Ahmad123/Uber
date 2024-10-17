package com.example.uber.data.repository

import com.example.uber.data.remote.GeoCode.GoogleMaps.GeoCodingGoogleMapsResponse
import com.example.uber.domain.model.DropOffLocation
import retrofit2.Response

interface IDropOffLocationRepository {
    suspend fun getDropOffLocation(): DropOffLocation
    suspend fun insertDropOffLocation(dropOffLocation: DropOffLocation)
    suspend fun deleteDropOffLocation()
    suspend fun getLocationFromGoogleApi(latitude: Double, longitude: Double): Response<GeoCodingGoogleMapsResponse>
}