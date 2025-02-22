package com.example.uber.domain.remote.location.model

import java.util.UUID

data class UpdateLocation(val riderId:UUID,val longitude: Double,val latitude:Double)
