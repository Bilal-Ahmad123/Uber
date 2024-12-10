package com.example.uber.data.repository

import com.example.uber.data.local.entities.Location
import okhttp3.WebSocketListener

interface ISocketRepository {
    fun connect(url:String,listener: WebSocketListener)
    fun send(location:Location)
    fun disconnect()
}