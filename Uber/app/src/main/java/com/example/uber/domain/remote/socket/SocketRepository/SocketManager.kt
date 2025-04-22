package com.example.uber.domain.remote.socket.SocketRepository

import android.util.Log
import com.example.uber.data.remote.api.backend.rider.socket.socketBroker.service.SocketBroker
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketManager @Inject constructor() : SocketBroker {
    private var hubConnection: HubConnection ? = null
    private val connectedToSocket = MutableSharedFlow<Boolean>()

    override fun connect(url: String) {
        runCatching {
            hubConnection = HubConnectionBuilder.create(url)
                .withTransport(com.microsoft.signalr.TransportEnum.LONG_POLLING)
                .build()
            hubConnection?.start()?.subscribe({
                CoroutineScope(Dispatchers.IO).launch {
                    connectedToSocket.emit(true)
                }
            }, { error ->
                Log.e("SocketManager", "Error starting connection: ${error.message}", error)
            })
        }.onFailure {
            Log.d("SocketManager", "Error connecting to socket: ${it.message}")
        }
    }

    override fun <T> send(message: T, method: String) {
        runCatching {
            hubConnection?.send(method, message)
        }.onFailure {
            Log.d("SocketManager", "Error sending message: ${it.message}")
        }
    }

    override fun disconnect(): Unit {
        hubConnection?.stop()
    }

    override fun getHubConnection(): HubConnection? = hubConnection

    override fun connectedToSocket(): Flow<Boolean> = connectedToSocket
}