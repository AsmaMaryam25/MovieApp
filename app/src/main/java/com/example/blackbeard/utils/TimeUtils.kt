package com.example.blackbeard.utils

object TimeUtils {

    fun convertMinutesToHoursAndMinutes(totalMinutes: Int): String {
        if(totalMinutes < 0) return "0 m"
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        if(minutes == 0 && hours == 0) {
            return "0 m"
        }

        if(hours < 1) {
            return "$minutes m"
        }

        if(minutes == 0) {
            return "$hours h"
        }

        return "$hours h $minutes m"
    }
}