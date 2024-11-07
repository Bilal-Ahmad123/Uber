package com.example.uber.data.remote.models.mapbox.geoCodeResponse

data class Feature(
    val geometry: Geometry,
    val id: String,
    val properties: Properties,
    val type: String
)