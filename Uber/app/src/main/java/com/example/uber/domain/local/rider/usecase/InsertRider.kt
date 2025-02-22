package com.example.uber.domain.local.rider.usecase

import com.example.uber.data.local.rider.repository.RiderRoomRepository
import com.example.uber.domain.local.rider.model.Rider
import javax.inject.Inject

class InsertRider @Inject constructor(private val repository: RiderRoomRepository) {
    suspend operator fun invoke(rider: Rider) {
        repository.insertRider(rider)
    }
}