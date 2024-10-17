package com.example.uber.data.local.DataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.uber.data.local.Dao.DropOffLocationDao
import com.example.uber.data.local.Dao.PickUpLocationDao
import com.example.uber.domain.model.DropOffLocation
import com.example.uber.domain.model.PickUpLocation

@Database(entities = [PickUpLocation::class, DropOffLocation::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pickUpLocationDao(): PickUpLocationDao
    abstract fun dropOffLocationDao(): DropOffLocationDao
}