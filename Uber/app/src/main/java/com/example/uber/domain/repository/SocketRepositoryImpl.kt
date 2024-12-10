package com.example.uber.domain.repository

import com.example.uber.data.local.entities.Location
import com.example.uber.data.remote.socket.SocketManager
import com.example.uber.data.repository.ISocketRepository
import okhttp3.WebSocketListener

class SocketRepositoryImpl(private val socketManager: SocketManager): ISocketRepository {
    override fun connect(url: String, listener: WebSocketListener) {
        socketManager.connect(url, listener)
    }

    override fun send(location: Location) {
        socketManager.send(location)
    }

    override fun disconnect() {
        socketManager.disconnect()
    }
}