package com.example.uber.domain.use_case.socket

import com.example.uber.data.local.entities.Location
import com.example.uber.data.repository.ISocketRepository
import okhttp3.WebSocketListener
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(private val socketRepository: ISocketRepository) {
    operator fun invoke(location: com.example.uber.data.local.models.Location) {
        socketRepository.send(location)
    }
}