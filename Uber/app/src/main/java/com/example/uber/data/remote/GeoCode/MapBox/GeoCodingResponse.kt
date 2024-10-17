package com.example.uber.data.remote.GeoCode.MapBox

data class GeoCodingResponse(
    val attribution: String,
    val features: List<Feature>,
    val type: String
)