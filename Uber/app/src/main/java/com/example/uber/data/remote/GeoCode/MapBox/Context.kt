package com.example.uber.data.remote.GeoCode.MapBox

data class Context(
    val address: Address,
    val country: Country,
    val district: District,
    val locality: Locality,
    val neighborhood: Neighborhood,
    val place: Place,
    val postcode: Postcode,
    val region: Region,
    val street: Street
)