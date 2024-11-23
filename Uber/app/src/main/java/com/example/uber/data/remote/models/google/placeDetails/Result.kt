package com.example.uber.data.remote.models.google.placeDetails

data class Result(
    val address_components: List<AddressComponent>,
    val adr_address: String,
    val formatted_address: String,
    val geometry: Geometry,
    val icon: String,
    val icon_background_color: String,
    val icon_mask_base_uri: String,
    val name: String,
    val place_id: String,
    val reference: String,
    val types: List<String>,
    val url: String,
    val utc_offset: Int,
    val vicinity: String
)