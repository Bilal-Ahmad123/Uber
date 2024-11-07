package com.example.uber.data.remote.models.mapbox.geoCodeResponse

data class Country(
    val country_code: String,
    val country_code_alpha_3: String,
    val mapbox_id: String,
    val name: String,
    val wikidata_id: String
)