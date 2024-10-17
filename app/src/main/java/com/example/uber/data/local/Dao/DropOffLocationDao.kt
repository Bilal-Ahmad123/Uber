package com.example.uber.data.local.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.uber.domain.model.DropOffLocation

@Dao
interface DropOffLocationDao {
    @Query("SELECT * FROM dropOffLocationTable")
    fun getDropOffLocation(): DropOffLocation

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDropOffLocation(dropOffLocation: DropOffLocation)

    @Query("DELETE FROM dropOffLocationTable where locationId = 0")
    fun deleteDropOffLocation()

}