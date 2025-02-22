package com.example.uber.data.remote.api.mapBox.models.geoCodeResponse

data class Feature(
    val geometry: Geometry,
    val id: String,
    val properties: Properties,
    val type: String
)