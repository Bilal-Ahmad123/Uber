package com.example.uber.data.remote.api.mapBox.models.RetrieveSuggestedPlaceDetail

data class Feature(
    val geometry: Geometry,
    val properties: Properties,
    val type: String
)