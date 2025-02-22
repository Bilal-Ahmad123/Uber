package com.example.uber.data.local.location.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.uber.data.local.location.dao.LocationDao
import com.example.uber.data.local.location.entities.Location

@Database(entities = [Location::class], version = 1)
@TypeConverters(LatLngJsonConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun currentLocationDao(): LocationDao
}