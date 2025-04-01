package com.example.uber.data.remote.api.backend.rider.general.model.response

data class NearbyVehicleDetails(val vehicleName : String,val imageUrl : String, val maxSeats : Int, val fare : Double)


data class NearbyVehiclesResponse(
    val nearbyVehicles: List<NearbyVehicleDetails>
)
