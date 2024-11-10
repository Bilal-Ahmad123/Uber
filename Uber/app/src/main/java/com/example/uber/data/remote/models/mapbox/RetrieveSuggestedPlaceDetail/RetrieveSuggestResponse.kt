package com.example.uber.data.remote.models.mapbox.RetrieveSuggestedPlaceDetail

data class RetrieveSuggestResponse(
    val attribution: String,
    val features: List<Feature>,
    val type: String
)