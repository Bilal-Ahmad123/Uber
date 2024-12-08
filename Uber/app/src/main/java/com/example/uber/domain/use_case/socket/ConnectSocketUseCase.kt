package com.example.uber.domain.use_case.socket

import com.example.uber.data.repository.ISocketRepository
import okhttp3.WebSocketListener
import javax.inject.Inject

class ConnectSocketUseCase @Inject constructor(private val socketRepository: ISocketRepository) {
    operator fun invoke(url: String, listener: WebSocketListener) {
        socketRepository.connect(url, listener)
    }
}