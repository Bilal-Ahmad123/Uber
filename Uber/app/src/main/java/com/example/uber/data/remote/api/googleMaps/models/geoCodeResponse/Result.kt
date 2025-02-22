package com.example.uber.data.remote.api.googleMaps.models.geoCodeResponse

data class Result(
    val address_components: List<AddressComponent>,
    val formatted_address: String,
    val geometry: Geometry,
    val place_id: String,
    val plus_code: PlusCode,
    val types: List<String>
)