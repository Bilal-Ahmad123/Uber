package com.example.uber.domain.use_case.auth

import com.example.uber.data.remote.api.backend.authentication.models.responseModels.CheckRiderExistsResponse
import com.example.uber.data.remote.api.backend.authentication.repository.IAuthRepository
import com.example.uber.domain.remote.authentication.model.response.CheckRiderExists
import retrofit2.Response
import javax.inject.Inject

class CheckIfUserExistsUseCase @Inject constructor(private val authRepository: IAuthRepository){
    suspend operator fun invoke(email:String):Response<CheckRiderExists> = authRepository.checkIfUserExists(email)
}