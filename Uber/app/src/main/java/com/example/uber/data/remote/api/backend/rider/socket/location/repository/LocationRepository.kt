package com.example.uber.data.remote.api.backend.rider.socket.location.repository

import com.example.uber.data.remote.api.backend.rider.socket.location.mapper.UpdateDriverLocation
import com.example.uber.domain.remote.socket.location.model.UpdateLocation
import kotlinx.coroutines.flow.Flow
import okhttp3.WebSocketListener

interface LocationRepository {
    fun send(location: UpdateLocation, method:String)
    fun startObservingDriverLocationUpdates()
    fun observeDriverLocation():Flow<UpdateDriverLocation>
}