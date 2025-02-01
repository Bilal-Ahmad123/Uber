package com.example.uber.domain.use_case.auth

import com.example.uber.data.remote.api.backend.authentication.models.ResponseModels.CheckRiderExistsResponse
import com.example.uber.data.repository.IAuthRepository
import retrofit2.Response
import javax.inject.Inject

class CheckIfUserExistsUseCase @Inject constructor(private val authRepository: IAuthRepository){
    suspend operator fun invoke(email:String):Response<CheckRiderExistsResponse> = authRepository.checkIfUserExists(email)
}