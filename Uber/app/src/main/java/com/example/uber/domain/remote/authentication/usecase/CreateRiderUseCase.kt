package com.example.uber.domain.remote.authentication.usecase

import com.example.uber.data.remote.api.backend.authentication.models.requestModels.RiderRequest
import com.example.uber.data.remote.api.backend.authentication.models.responseModels.RiderResponse
import com.example.uber.data.remote.api.backend.authentication.repository.IAuthRepository
import retrofit2.Response
import javax.inject.Inject

class CreateRiderUseCase @Inject constructor(private val authRepository: IAuthRepository) {
     suspend operator fun invoke(rider: RiderRequest): Response<RiderResponse> = authRepository.registerRider(rider)
}