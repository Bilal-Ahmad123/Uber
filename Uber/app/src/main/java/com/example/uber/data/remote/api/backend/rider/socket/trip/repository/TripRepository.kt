package com.example.uber.data.remote.api.backend.rider.socket.trip.repository

import com.example.uber.data.remote.api.backend.rider.socket.ride.model.TripLocation
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun observeTrip(): Flow<TripLocation>
}