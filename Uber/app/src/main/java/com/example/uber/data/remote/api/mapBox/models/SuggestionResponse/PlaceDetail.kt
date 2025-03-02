package com.example.uber.data.remote.api.mapBox.models.SuggestionResponse

import java.util.UUID

data class PlaceDetail(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val fullAddress: String,
    val googleId: String
)
