package com.example.uber.data.remote.api.backend.rider.location.repository

import com.example.uber.data.local.location.models.Location
import okhttp3.WebSocketListener

interface SocketRepository {
    fun connect(url:String,listener: WebSocketListener)
    fun send(location: Location)
    fun disconnect()
}