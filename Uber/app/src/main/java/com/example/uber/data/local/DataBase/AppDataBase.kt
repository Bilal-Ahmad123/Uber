package com.example.uber.data.local.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.uber.data.local.dao.LocationDao
import com.example.uber.data.local.entities.Location

@Database(entities = [Location::class], version = 1)
@TypeConverters(LatLngJsonConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun currentLocationDao(): LocationDao
}