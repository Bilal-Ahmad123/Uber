package com.example.uber.data.remote.api.backend.rider.location.api

import android.util.Log
import com.example.uber.data.remote.api.backend.rider.location.mapper.UpdateDriverLocation
import com.example.uber.data.remote.api.backend.rider.location.mapper.toData
import com.example.uber.domain.remote.location.model.UpdateLocation
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.UUID
import javax.inject.Inject

class SocketManager @Inject constructor() {
    private var socket: WebSocket? = null
    private val client = OkHttpClient()
    private lateinit var hubConnection: HubConnection
    private val driver = MutableSharedFlow<UpdateDriverLocation>()
    val driverLocationUpdates = driver.asSharedFlow()

    fun connect(url: String, listener: WebSocketListener) {
        runCatching {
            hubConnection = HubConnectionBuilder.create(url)
                .withTransport(com.microsoft.signalr.TransportEnum.LONG_POLLING)
                .build()
            hubConnection.start().subscribe({
                observeDriverLocationUpdates()
            }, { error ->
                Log.e("SocketManager", "Error starting connection: ${error.message}", error)
            })
        }.onFailure {
            Log.d("SocketManager", "Error connecting to socket: ${it.message}")
        }
    }

    fun send(location: UpdateLocation) {
        runCatching {
            hubConnection.send("UpdateLocation", location.toData())
        }.onFailure {
            Log.d("SocketManager", "Error sending message: ${it.message}")
        }
    }

    fun observeDriverLocationUpdates() {
        runCatching {
            if (::hubConnection.isInitialized) {
                Log.d("SocketManager", "Observing driver location updates")
                hubConnection?.on(
                    "DriverLocationUpdate",
                    { userId: String, longitude: Double, latitude: Double,vehicleType:String ->
                        CoroutineScope(Dispatchers.IO).launch {
                            Log.d("SocketManager", "Driver location update received: $userId, $longitude, $latitude")
                            driver.emit(UpdateDriverLocation(UUID.fromString(userId), longitude, latitude, vehicleType))
                        }
                    },
                    String::class.java,
                    Double::class.java,
                    Double::class.java,
                    String::class.java
                )
            }
        }.onFailure {
            Log.d("SocketManager", "Error observing driver location updates: ${it.message}")
        }
    }

    fun disconnect() = hubConnection.stop()
}