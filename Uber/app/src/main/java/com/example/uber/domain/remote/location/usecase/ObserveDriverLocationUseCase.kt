package com.example.uber.domain.remote.location.usecase

import com.example.uber.data.remote.api.backend.rider.location.mapper.UpdateDriverLocation
import com.example.uber.data.remote.api.backend.rider.location.repository.SocketRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDriverLocationUseCase @Inject constructor(
    private val repository: SocketRepository
) {
    suspend operator fun invoke(): Flow<UpdateDriverLocation> {
        return repository.observeDriverLocation()
    }
}