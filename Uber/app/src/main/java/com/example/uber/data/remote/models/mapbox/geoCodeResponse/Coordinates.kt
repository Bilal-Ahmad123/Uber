package com.example.uber.data.remote.models.mapbox.geoCodeResponse

data class Coordinates(
    val accuracy: String,
    val latitude: Double,
    val longitude: Double,
    val routable_points: List<RoutablePoint>
)