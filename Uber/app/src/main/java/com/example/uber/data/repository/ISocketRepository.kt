package com.example.uber.data.repository

import okhttp3.WebSocketListener

interface ISocketRepository {
    fun connect(url:String,listener: WebSocketListener)
    fun send(message:String)
    fun disconnect()
}