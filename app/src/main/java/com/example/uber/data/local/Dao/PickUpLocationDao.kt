package com.example.uber.data.local.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.uber.domain.model.PickUpLocation

@Dao
interface PickUpLocationDao {
    @Query("SELECT * FROM pickUpLocationTable")
    fun getPickUpLocation(): PickUpLocation

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPickUpLocation(pickUpLocation: PickUpLocation)

    @Query("DELETE FROM pickUpLocationTable where locationId = 0")
    fun deletePickUpLocation()

}