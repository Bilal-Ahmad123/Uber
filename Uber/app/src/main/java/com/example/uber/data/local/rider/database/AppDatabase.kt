package com.example.uber.data.local.rider.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.uberdriver.data.local.driver.dao.RiderDao
import com.example.uber.data.local.rider.entities.Rider
import com.example.uber.data.local.rider.typeconverter.RiderTypeConverter

@TypeConverters(RiderTypeConverter::class)
@Database(entities = [Rider::class], version = 1)
abstract class AppDatabase:RoomDatabase() {
    abstract fun getRiderDao():RiderDao
}