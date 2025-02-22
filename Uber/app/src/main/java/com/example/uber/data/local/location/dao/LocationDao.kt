package com.example.uber.data.local.location.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.uber.data.local.location.entities.Location

@Dao
interface LocationDao {
    @Query("SELECT * FROM locationTable")
    fun getCurrentLocation(): Location

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrentLocation(pickUpLocation: Location)

    @Query("DELETE FROM locationTable where locationId = 0")
    fun deleteCurrentLocation()
}