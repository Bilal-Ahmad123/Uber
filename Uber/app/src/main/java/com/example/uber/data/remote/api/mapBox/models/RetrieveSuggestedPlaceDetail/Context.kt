package com.example.uber.data.remote.api.mapBox.models.RetrieveSuggestedPlaceDetail

data class Context(
    val address: Address,
    val country: Country,
    val locality: Locality,
    val place: Place,
    val postcode: Postcode,
    val street: Street
)