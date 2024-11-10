package com.example.uber.data.remote.models.mapbox.RetrieveSuggestedPlaceDetail

data class Feature(
    val geometry: Geometry,
    val properties: Properties,
    val type: String
)