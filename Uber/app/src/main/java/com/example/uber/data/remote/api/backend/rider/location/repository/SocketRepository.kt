package com.example.uber.data.remote.api.backend.rider.location.repository

import com.example.uber.data.local.location.models.Location
import com.example.uber.domain.remote.location.model.UpdateLocation
import okhttp3.WebSocketListener

interface SocketRepository {
    fun connect(url:String,listener: WebSocketListener)
    fun send(location: UpdateLocation)
    fun disconnect()
}