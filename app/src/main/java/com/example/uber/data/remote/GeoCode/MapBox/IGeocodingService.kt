package com.example.uber.data.remote.GeoCode.MapBox
import com.example.uber.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IGeocodingService {
    @GET("search/geocode/v6/reverse")
    suspend fun geoCodeLocation(
        @Query("latitude") latitude:Double,
        @Query("longitude") longitude:Double,
        @Query("access_token") accessToken: String =  BuildConfig.MAPBOX_TOKEN
    ): Response<GeoCodingResponse>
}