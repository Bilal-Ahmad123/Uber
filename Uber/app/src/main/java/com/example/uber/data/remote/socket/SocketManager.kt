package com.example.uber.data.remote.socket

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

class SocketManager @Inject constructor() {
    private var socket: WebSocket? = null
    fun connect(url:String,listener:WebSocketListener){
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        socket = client.newWebSocket(request,listener)
    }
    fun send(message:String) = socket?.send(message)
    fun disconnect() = socket?.close(1000,"Disconnected")
}