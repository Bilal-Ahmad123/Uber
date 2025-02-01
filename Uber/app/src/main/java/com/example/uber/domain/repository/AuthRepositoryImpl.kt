package com.example.uber.domain.repository

import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.data.remote.api.backend.authentication.IAuthenticationService
import com.example.uber.data.remote.api.backend.authentication.models.RequestModels.RiderRequest
import com.example.uber.data.remote.api.backend.authentication.models.ResponseModels.CheckRiderExistsResponse
import com.example.uber.data.remote.api.backend.authentication.models.ResponseModels.RiderResponse
import com.example.uber.data.repository.IAuthRepository
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import okhttp3.ResponseBody
import retrofit2.Response

class AuthRepositoryImpl(private val api: IAuthenticationService):IAuthRepository{
    private  var auth: FirebaseAuth = FirebaseAuth.getInstance()
    override suspend fun signIn(task: SignInCredential):Result<FirebaseUser?> {
        var user: FirebaseUser? = null
        return try {
            val credential = GoogleAuthProvider.getCredential(task.googleIdToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user = auth.currentUser!!
                    }
                }.await()
            Result.success(user)
        } catch (exp: Exception) {
            Result.failure(exp)
        }
    }

    override suspend fun checkIfUserExists(email:String): Response<CheckRiderExistsResponse> {
        return try {
           api.checkUserExists(email)
        }catch (e:Exception){
            Response.error(500,ResponseBody.create(null, "Network error: ${e.localizedMessage}"))
        }

    }

    override suspend fun registerRider(rider: RiderRequest):Response<RiderResponse> {
        return try{
            api.registerRider(rider)
        }
        catch (e:Exception){
            Response.error(500,ResponseBody.create(null, "Network error: ${e.localizedMessage}"))
        }
    }
}