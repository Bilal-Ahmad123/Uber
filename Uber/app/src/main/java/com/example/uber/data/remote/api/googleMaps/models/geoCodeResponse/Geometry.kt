package com.example.uber.data.remote.api.googleMaps.models.geoCodeResponse

data class Geometry(
    val bounds: Bounds,
    val location: Location,
    val location_type: String,
    val viewport: Viewport
)