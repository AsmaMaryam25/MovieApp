package com.example.blackbeard.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TimeUtilsTest {

    @Test
    fun convertMinutesToHoursAndMinutes() {
        val minutesList = listOf(
            -1,
            0,
            59,
            60,
            61,
        )

        val expectedValues = listOf(
            "0 m",
            "0 m",
            "59 m",
            "1 h",
            "1 h 1 m"
        )

        minutesList.zip(expectedValues).forEach { (value, expectedValue) ->
            assertEquals(expectedValue, TimeUtils.convertMinutesToHoursAndMinutes(value))
        }
    }
}