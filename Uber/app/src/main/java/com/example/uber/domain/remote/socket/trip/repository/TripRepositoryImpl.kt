package com.example.uber.domain.remote.socket.trip.repository

import com.example.uber.core.utils.SocketMethods
import com.example.uber.data.remote.api.backend.rider.socket.ride.model.TripLocation
import com.example.uber.data.remote.api.backend.rider.socket.socketBroker.service.SocketBroker
import com.example.uber.data.remote.api.backend.rider.socket.trip.repository.TripRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class TripRepositoryImpl @Inject constructor(private val socketManager: SocketBroker) :
    TripRepository {
    private val socketScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val tripLocations = MutableSharedFlow<TripLocation>()
    override fun observeTrip(): Flow<TripLocation> {
        socketManager.apply {
            getHubConnection()?.let {
                it.on(
                    SocketMethods.TRIP_UPDATES,
                    { rideId: String, driverId: String, latitude: Double, longitude: Double, time: Int, distance: Int ->
                        socketScope.launch {
                            tripLocations.emit(
                                TripLocation(
                                    UUID.fromString(rideId),
                                    UUID.fromString(driverId),
                                    latitude,
                                    longitude,
                                    time,
                                    distance
                                )
                            )
                        }
                    },
                    String::class.java,
                    String::class.java,
                    Double::class.java,
                    Double::class.java,
                    Int::class.java,
                    Int::class.java
                )
            }
        }
        return tripLocations
    }
}