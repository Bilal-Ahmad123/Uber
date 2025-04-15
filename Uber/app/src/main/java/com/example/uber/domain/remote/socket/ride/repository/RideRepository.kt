package com.example.uber.domain.remote.socket.ride.repository

import com.example.uber.core.utils.SocketMethods
import com.example.uber.domain.remote.socket.SocketRepository.SocketManager
import com.example.uber.data.remote.api.backend.rider.socket.ride.model.RideRequest
import com.example.uber.data.remote.api.backend.rider.socket.ride.repository.RideRepository
import com.example.uber.data.remote.api.backend.rider.socket.socketBroker.service.SocketBroker
import javax.inject.Inject

class RideRepository @Inject constructor(private val socketManager: SocketBroker) : RideRepository {
    override fun sendRideRequest(rideRequest: RideRequest) {
        socketManager.send(rideRequest,SocketMethods.REQUEST_RIDE)
    }

    override fun observeRideAcceptedEvent() {

    }
}