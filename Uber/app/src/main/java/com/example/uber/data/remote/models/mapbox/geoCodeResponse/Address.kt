package com.example.uber.data.remote.models.mapbox.geoCodeResponse

data class Address(
    val address_number: String,
    val mapbox_id: String,
    val name: String,
    val street_name: String
)