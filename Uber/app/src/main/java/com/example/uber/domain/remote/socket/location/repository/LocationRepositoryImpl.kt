package com.example.uber.domain.remote.socket.location.repository

import android.util.Log
import com.example.uber.data.remote.api.backend.rider.socket.location.mapper.UpdateDriverLocation
import com.example.uber.data.remote.api.backend.rider.socket.location.repository.LocationRepository
import com.example.uber.data.remote.api.backend.rider.socket.socketBroker.service.SocketBroker
import com.example.uber.domain.remote.socket.location.model.UpdateLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(private val socketManager: SocketBroker): LocationRepository {
    private val driver = MutableSharedFlow<UpdateDriverLocation>()
    private val driverLocationUpdates = driver.asSharedFlow()

    override fun send(location: UpdateLocation, method: String) {
        socketManager.send(location,"UpdateLocation")
    }

    override fun startObservingDriverLocationUpdates() {
        runCatching {
            socketManager.apply {
                if(getHubConnection() != null){
                    getHubConnection()?.on("DriverLocationUpdate",
                        { userId: String, longitude: Double, latitude: Double, vehicleType: String ->
                            CoroutineScope(Dispatchers.IO).launch {
                                Log.d(
                                    "SocketManager",
                                    "Driver location update received: $userId, $longitude, $latitude"
                                )
                                driver.emit(
                                    UpdateDriverLocation(
                                        UUID.fromString(userId),
                                        longitude,
                                        latitude,
                                        vehicleType
                                    )
                                )
                            }
                        },
                        String::class.java,
                        Double::class.java,
                        Double::class.java,
                        String::class.java
                        )
                }
            }
        }.onFailure {
            Log.d("SocketManager", "Error observing driver location updates: ${it.message}")
        }
    }

    override fun observeDriverLocation():Flow<UpdateDriverLocation> {
        return driverLocationUpdates
    }
}