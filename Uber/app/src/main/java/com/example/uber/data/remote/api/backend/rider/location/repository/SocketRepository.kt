package com.example.uber.data.remote.api.backend.rider.location.repository

import com.example.uber.data.remote.api.backend.rider.location.mapper.UpdateDriverLocation
import com.example.uber.domain.remote.location.model.UpdateLocation
import kotlinx.coroutines.flow.Flow
import okhttp3.WebSocketListener

interface SocketRepository {
    fun connect(url:String,listener: WebSocketListener)
    fun send(location: UpdateLocation)
    fun disconnect()
    fun startObservingDriverLocationUpdates()
    fun observeDriverLocation():Flow<UpdateDriverLocation>
}