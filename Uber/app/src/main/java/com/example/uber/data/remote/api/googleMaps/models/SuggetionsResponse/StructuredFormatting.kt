package com.example.uber.data.remote.api.googleMaps.models.SuggetionsResponse

data class StructuredFormatting(
    val main_text: String,
    val main_text_matched_substrings: List<MainTextMatchedSubstring>,
    val secondary_text: String
)