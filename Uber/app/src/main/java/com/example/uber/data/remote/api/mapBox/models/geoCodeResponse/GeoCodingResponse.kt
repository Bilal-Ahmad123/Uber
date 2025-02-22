package com.example.uber.data.remote.api.mapBox.models.geoCodeResponse

data class GeoCodingResponse(
    val attribution: String,
    val features: List<Feature>,
    val type: String
)