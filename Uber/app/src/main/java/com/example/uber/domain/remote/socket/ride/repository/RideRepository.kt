package com.example.uber.domain.remote.socket.ride.repository

import com.example.uber.domain.remote.socket.SocketRepository.SocketManager
import com.example.uber.data.remote.api.backend.rider.socket.ride.model.RideRequest
import com.example.uber.data.remote.api.backend.rider.socket.ride.repository.RideRepository
import javax.inject.Inject

class RideRepository @Inject constructor(private val socketManager: SocketManager) : RideRepository {
    override fun sendRideRequest(rideRequest: RideRequest) {
        socketManager.send(rideRequest,"RequestRide")
    }

    override fun observeRideAcceptedEvent() {
        TODO("Not yet implemented")
    }
}