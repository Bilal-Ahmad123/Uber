package com.example.uber.core.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object StringHelper {
    fun calculateTimeWithVehicleTime(time : Int):String{
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, time)

        val formatter = SimpleDateFormat("h:mma", Locale.getDefault())
        val formattedTime = formatter.format(calendar.time).lowercase()
        return formattedTime
    }

    fun showTimeAway(time : Int):String{
        return "${time} min away"
    }

    fun showCurrency(fare : Double, currency : String = "PKR"):String{
        return "${currency} ${fare}"
    }

}