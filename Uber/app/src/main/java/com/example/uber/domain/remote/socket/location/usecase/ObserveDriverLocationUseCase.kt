package com.example.uber.domain.remote.socket.location.usecase

import com.example.uber.data.remote.api.backend.rider.socket.location.mapper.UpdateDriverLocation
import com.example.uber.data.remote.api.backend.rider.socket.location.repository.SocketRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDriverLocationUseCase @Inject constructor(
    private val repository: SocketRepository
) {
    suspend operator fun invoke(): Flow<UpdateDriverLocation> {
        return repository.observeDriverLocation()
    }
}