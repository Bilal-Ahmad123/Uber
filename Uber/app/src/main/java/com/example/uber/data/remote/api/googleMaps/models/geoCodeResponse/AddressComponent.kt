package com.example.uber.data.remote.api.googleMaps.models.geoCodeResponse

data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String>
)