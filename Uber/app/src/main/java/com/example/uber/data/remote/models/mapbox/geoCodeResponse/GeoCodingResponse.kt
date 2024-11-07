package com.example.uber.data.remote.models.mapbox.geoCodeResponse

data class GeoCodingResponse(
    val attribution: String,
    val features: List<Feature>,
    val type: String
)