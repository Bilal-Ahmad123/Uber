package com.example.uber.data.remote.api.mapBox.models.SuggestionResponse

data class Context(
    val address: Address,
    val country: Country,
    val neighborhood: Neighborhood,
    val place: Place,
    val postcode: Postcode,
    val region: Region,
    val street: Street
)