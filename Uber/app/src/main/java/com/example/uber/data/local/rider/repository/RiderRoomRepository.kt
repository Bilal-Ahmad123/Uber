package com.example.uber.data.local.rider.repository

import com.example.uber.domain.local.rider.model.Rider


interface RiderRoomRepository {
     suspend fun getRider(): Rider
     suspend fun insertRider(rider:Rider)
}