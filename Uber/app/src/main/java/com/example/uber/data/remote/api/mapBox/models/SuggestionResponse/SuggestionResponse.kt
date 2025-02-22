package com.example.uber.data.remote.api.mapBox.models.SuggestionResponse

data class SuggestionResponse(
    val attribution: String,
    val response_id: String,
    val suggestions: List<Suggestion>
)