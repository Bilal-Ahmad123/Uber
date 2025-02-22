package com.example.uber.data.remote.api.googleMaps.models.directionsResponse

data class DirectionsResponse(
    val geocoded_waypoints: List<GeocodedWaypoint>,
    val routes: List<Route>,
    val status: String
)