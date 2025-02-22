package com.example.uber.data.remote.api.mapBox.models.RetrieveSuggestedPlaceDetail

data class RetrieveSuggestResponse(
    val attribution: String,
    val features: List<Feature>,
    val type: String
)