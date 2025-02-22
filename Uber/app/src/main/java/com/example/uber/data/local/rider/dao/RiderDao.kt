package com.example.uberdriver.data.local.driver.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.uber.data.local.rider.entities.Rider

@Dao
interface RiderDao {
    @Query("SELECT * from rider order by riderId limit 1")
    fun getRider(): Rider

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDriver(driver: Rider)
}