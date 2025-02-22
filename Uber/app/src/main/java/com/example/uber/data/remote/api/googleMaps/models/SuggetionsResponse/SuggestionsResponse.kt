package com.example.uber.data.remote.api.googleMaps.models.SuggetionsResponse

data class SuggestionsResponse(
    val predictions: List<Prediction>,
    val status: String
)