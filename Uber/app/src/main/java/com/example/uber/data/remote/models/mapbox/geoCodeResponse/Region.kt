package com.example.uber.data.remote.models.mapbox.geoCodeResponse

data class Region(
    val mapbox_id: String,
    val name: String,
    val region_code: String,
    val region_code_full: String,
    val wikidata_id: String
)