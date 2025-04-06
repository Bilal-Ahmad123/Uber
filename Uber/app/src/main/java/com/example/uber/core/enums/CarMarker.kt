package com.example.uber.core.enums

enum class CarMarker(private val displayName : String) {
    UberXL("UberXL"),
    UberX("UberX"),
    Uber_Lux("Uber Lux");

    override fun toString(): String = displayName
}