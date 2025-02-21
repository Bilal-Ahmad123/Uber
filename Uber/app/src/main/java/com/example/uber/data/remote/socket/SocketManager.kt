package com.example.uber.data.remote.socket

import android.util.Log
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

class SocketManager @Inject constructor() {
    private var socket: WebSocket? = null
    private val client = OkHttpClient()
    private lateinit var hubConnection: HubConnection

    fun connect(url:String,listener:WebSocketListener){
        runCatching {
            hubConnection = HubConnectionBuilder.create(url)
                .withTransport(com.microsoft.signalr.TransportEnum.LONG_POLLING)
                .build()
            hubConnection.start().blockingAwait()
        }.onFailure {
            Log.d("SocketManager", "Error connecting to socket: ${it.message}")
        }
    }
    fun send(location: com.example.uber.data.local.models.Location){
        runCatching {
            hubConnection.send("UpdateLocation", location)
        }.onFailure {
            Log.d("SocketManager", "Error sending message: ${it.message}")
        }
    }
    fun disconnect() = hubConnection.stop()
}