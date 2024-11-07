package com.example.uber.data.remote.models.google.geoCodeResponse

data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String>
)