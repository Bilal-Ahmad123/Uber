package com.example.uber.data.remote.models.google.SuggetionsResponse

data class SuggestionsResponse(
    val predictions: List<Prediction>,
    val status: String
)