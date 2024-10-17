package com.example.uber.data.remote.GeoCode.GoogleMaps

data class GeoCodingGoogleMapsResponse(
    val plus_code: PlusCode,
    val results: List<Result>,
    val status: String
)