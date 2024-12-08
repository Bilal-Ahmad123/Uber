package com.example.uber.domain.use_case.socket

import com.example.uber.data.repository.ISocketRepository
import javax.inject.Inject

class DisconnectSocketUseCase @Inject constructor(private val socketRepository: ISocketRepository) {
    operator fun invoke() {
        socketRepository.disconnect()
    }
}