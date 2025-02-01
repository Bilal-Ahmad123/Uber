package com.example.uber.data.repository

import com.example.uber.data.remote.api.backend.authentication.models.RequestModels.RiderRequest
import com.example.uber.data.remote.api.backend.authentication.models.ResponseModels.CheckRiderExistsResponse
import com.example.uber.data.remote.api.backend.authentication.models.ResponseModels.RiderResponse
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseUser
import retrofit2.Response

interface IAuthRepository {
     suspend fun signIn(task: SignInCredential):Result<FirebaseUser?>
     suspend fun checkIfUserExists(email:String): retrofit2.Response<CheckRiderExistsResponse>
     suspend fun registerRider(rider: RiderRequest): Response<RiderResponse>
}