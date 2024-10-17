package com.example.uber.data.remote.GeoCode.GoogleMaps
import com.example.uber.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IGeocodingGoogleMapService {
    @GET("maps/api/geocode/json")
    suspend fun geoCodeLocation(
        @Query("latlng") latitude:String,
        @Query("key") accessToken: String =  BuildConfig.GOOGLE_API_KEY
    ): Response<GeoCodingGoogleMapsResponse>
}