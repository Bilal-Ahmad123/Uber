package com.example.uber.data.remote.api.mapBox.models.RetrieveSuggestedPlaceDetail

data class Coordinates(
    val latitude: Double,
    val longitude: Double,
    val routable_points: List<RoutablePoint>
)