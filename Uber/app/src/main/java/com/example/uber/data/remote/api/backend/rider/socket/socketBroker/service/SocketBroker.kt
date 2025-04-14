package com.example.uber.data.remote.api.backend.rider.socket.socketBroker.service

import com.microsoft.signalr.HubConnection
import kotlinx.coroutines.flow.Flow

interface SocketBroker {
    fun connect(url:String)
    fun <T>send(message: T, method:String)
    fun disconnect()
    fun connectedToSocket(): Flow<Boolean>
    fun getHubConnection():HubConnection?
}