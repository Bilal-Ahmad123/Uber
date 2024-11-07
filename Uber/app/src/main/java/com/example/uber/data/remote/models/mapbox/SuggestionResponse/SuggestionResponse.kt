package com.example.uber.data.remote.models.mapbox.SuggestionResponse

data class SuggestionResponse(
    val attribution: String,
    val response_id: String,
    val suggestions: List<Suggestion>
)