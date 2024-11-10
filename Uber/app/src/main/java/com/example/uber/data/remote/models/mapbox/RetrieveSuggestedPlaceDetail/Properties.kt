package com.example.uber.data.remote.models.mapbox.RetrieveSuggestedPlaceDetail

data class Properties(
    val address: String,
    val context: Context,
    val coordinates: Coordinates,
    val external_ids: ExternalIds,
    val feature_type: String,
    val full_address: String,
    val language: String,
    val maki: String,
    val mapbox_id: String,
    val metadata: Metadata,
    val name: String,
    val operational_status: String,
    val place_formatted: String,
    val poi_category: List<String>,
    val poi_category_ids: List<String>
)