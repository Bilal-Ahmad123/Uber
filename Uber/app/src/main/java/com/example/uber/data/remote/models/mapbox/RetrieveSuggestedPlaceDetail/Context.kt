package com.example.uber.data.remote.models.mapbox.RetrieveSuggestedPlaceDetail

data class Context(
    val address: Address,
    val country: Country,
    val locality: Locality,
    val place: Place,
    val postcode: Postcode,
    val street: Street
)