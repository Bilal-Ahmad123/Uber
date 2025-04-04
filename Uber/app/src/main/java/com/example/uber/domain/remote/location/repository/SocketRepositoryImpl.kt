package com.example.uber.domain.remote.location.repository

import com.example.uber.data.local.location.models.Location
import com.example.uber.data.remote.api.backend.rider.location.api.SocketManager
import com.example.uber.data.remote.api.backend.rider.location.mapper.UpdateDriverLocation
import com.example.uber.data.remote.api.backend.rider.location.repository.SocketRepository
import com.example.uber.domain.remote.location.model.UpdateLocation
import kotlinx.coroutines.flow.Flow
import okhttp3.WebSocketListener

class SocketRepositoryImpl(private val socketManager: SocketManager): SocketRepository {
    override fun connect(url: String, listener: WebSocketListener) {
        socketManager.connect(url, listener)
    }

    override fun send(location: UpdateLocation) {
        socketManager.send(location)
    }

    override fun disconnect() {
        socketManager.disconnect()
    }

    override fun startObservingDriverLocationUpdates() {
       socketManager.observeDriverLocationUpdates()
    }

    override fun observeDriverLocation():Flow<UpdateDriverLocation> {
        return socketManager.driverLocationUpdates
    }

    override fun connectedToSocket():Flow<Boolean>{
        return socketManager.socketConnected
    }

}