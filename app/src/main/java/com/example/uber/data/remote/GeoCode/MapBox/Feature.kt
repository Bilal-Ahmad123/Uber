package com.example.uber.data.remote.GeoCode.MapBox

data class Feature(
    val geometry: Geometry,
    val id: String,
    val properties: Properties,
    val type: String
)