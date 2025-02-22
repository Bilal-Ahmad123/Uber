package com.example.uber.data.local.rider.typeconverter

import androidx.room.TypeConverter
import java.util.UUID

class RiderTypeConverter {

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? { 
        return uuid?.let { UUID.fromString(it) }
    }
}
