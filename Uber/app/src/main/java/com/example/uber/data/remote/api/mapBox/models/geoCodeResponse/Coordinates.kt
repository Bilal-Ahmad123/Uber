package com.example.uber.data.remote.api.mapBox.models.geoCodeResponse

data class Coordinates(
    val accuracy: String,
    val latitude: Double,
    val longitude: Double,
    val routable_points: List<RoutablePoint>
)