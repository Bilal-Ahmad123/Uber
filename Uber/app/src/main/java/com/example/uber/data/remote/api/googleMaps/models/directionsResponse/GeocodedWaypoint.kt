package com.example.uber.data.remote.api.googleMaps.models.directionsResponse

data class GeocodedWaypoint(
    val geocoder_status: String,
    val place_id: String,
    val types: List<String>
)