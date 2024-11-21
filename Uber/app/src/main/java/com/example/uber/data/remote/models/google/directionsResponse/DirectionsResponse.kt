package com.example.uber.data.remote.models.google.directionsResponse

data class DirectionsResponse(
    val geocoded_waypoints: List<GeocodedWaypoint>,
    val routes: List<Route>,
    val status: String
)