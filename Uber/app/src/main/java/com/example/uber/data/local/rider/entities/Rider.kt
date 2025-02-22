package com.example.uber.data.local.rider.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "rider")
data class Rider (
    @PrimaryKey val riderId: UUID
)