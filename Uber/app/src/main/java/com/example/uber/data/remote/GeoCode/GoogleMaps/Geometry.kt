package com.example.uber.data.remote.GeoCode.GoogleMaps

data class Geometry(
    val bounds: Bounds,
    val location: Location,
    val location_type: String,
    val viewport: Viewport
)