package com.example.uber.data.remote.api.backend.authentication

import com.example.uber.data.remote.api.backend.authentication.models.RequestModels.RiderDetailsRequest
import com.example.uber.data.remote.api.backend.authentication.models.RequestModels.RiderRequest
import com.example.uber.data.remote.api.backend.authentication.models.ResponseModels.CheckRiderExistsResponse
import com.example.uber.data.remote.api.backend.authentication.models.ResponseModels.RiderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IAuthenticationService {
    @POST("api/create/rider")
    suspend fun registerRider(
        @Body Rider: RiderDetailsRequest
    ):Response<RiderResponse>

    @GET("api/rider/exists")
    suspend fun checkUserExists(
        @Query("email") email:String
    ):Response<CheckRiderExistsResponse>
}