package com.example.uber.domain.remote.location.usecase

import com.example.uber.data.remote.api.backend.rider.location.api.SocketManager
import com.example.uber.data.remote.api.backend.rider.location.repository.SocketRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveConnectedToSocket @Inject constructor(private val socketRepository: SocketRepository) {
    operator fun invoke():Flow<Boolean>{
        return socketRepository.connectedToSocket()
    }
}