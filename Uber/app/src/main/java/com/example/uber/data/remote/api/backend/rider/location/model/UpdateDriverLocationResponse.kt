package com.example.uber.data.remote.api.backend.rider.location.model

import java.util.UUID

data class UpdateDriverLocationResponse(val userId: UUID, val longitude: Double, val latitude: Double, val vehicleType: String)