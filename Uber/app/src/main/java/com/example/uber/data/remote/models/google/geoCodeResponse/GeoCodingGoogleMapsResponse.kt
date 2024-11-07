package com.example.uber.data.remote.models.google.geoCodeResponse

data class GeoCodingGoogleMapsResponse(
    val plus_code: PlusCode,
    val results: List<Result>,
    val status: String
)