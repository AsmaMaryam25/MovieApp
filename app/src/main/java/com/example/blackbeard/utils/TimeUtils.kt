package com.example.blackbeard.utils

object TimeUtils {

    fun convertMinutesToHoursAndMinutes(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        if(hours < 1) {
            return "$minutes m"
        }

        if(minutes == 0) {
            return "$hours h"
        }

        return "$hours h $minutes m"
    }
}