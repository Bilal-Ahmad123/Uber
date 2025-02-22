package com.example.uber.domain.local.rider.repository

import android.util.Log
import com.example.uber.data.local.rider.mapper.toDomainModel
import com.example.uber.data.local.rider.mapper.toEntityModel
import com.example.uber.data.local.rider.repository.RiderRoomRepository
import com.example.uber.domain.local.rider.model.Rider
import com.example.uberdriver.data.local.driver.dao.RiderDao
import javax.inject.Inject

class RiderRoomRepository @Inject constructor(private val riderDao: RiderDao) :
    RiderRoomRepository {
    override suspend fun getRider(): Rider {
        return try {
            Log.d("TAG", "getRider: ${riderDao.getRider().toDomainModel().riderId}")
            riderDao.getRider().toDomainModel()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun insertRider(rider: Rider) {
        riderDao.insertDriver(rider.toEntityModel())
    }
}