package com.example.uber.domain.use_case.auth

import com.example.uber.data.remote.api.backend.authentication.models.RequestModels.RiderRequest
import com.example.uber.data.remote.api.backend.authentication.models.ResponseModels.RiderResponse
import com.example.uber.data.repository.IAuthRepository
import retrofit2.Response
import javax.inject.Inject

class CreateRiderUseCase @Inject constructor(private val authRepository: IAuthRepository) {
     suspend operator fun invoke(rider: RiderRequest): Response<RiderResponse> = authRepository.registerRider(rider)
}