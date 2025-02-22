package com.example.uber.data.local.rider.mapper

import com.example.uber.data.local.rider.entities.Rider


fun Rider.toDomainModel():com.example.uber.domain.local.rider.model.Rider{
    return com.example.uber.domain.local.rider.model.Rider(riderId)
}

fun com.example.uber.domain.local.rider.model.Rider.toEntityModel(): Rider {
    return Rider(riderId)
}