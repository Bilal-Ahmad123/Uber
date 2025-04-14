package com.example.uber.data.remote.api.backend.rider.socket.ride.model

import java.util.UUID

data class RideRequest(val riderId :UUID, val pickupLongitude:Double,val pickupLatitude : Double,val dropOffLatitude : Double, val dropOffLongitude : Double)
