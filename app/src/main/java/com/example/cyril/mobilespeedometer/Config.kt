package com.example.cyril.mobilespeedometer

import java.text.SimpleDateFormat

class Config {

    companion object {
        const val HELLO = "Привет, дружище!"
        const val DATABASE_VER = 1
        const val DATABASE_NAME =  "RESULTS.db"

        //Table
        const val TABLE_NAME = "Results"
        const val COL_ID = "Id"
        const val COL_DATE = "Date"
        const val COL_TIME = "Time"
        const val COL_RESULT_TIME = "ResultTime"

        //update-time for locationManager (how often there will be any update)
        const val FAST_INTERVAL = 10L
        const val SLOW_INTERVAL = 1000L

        //update-distance for locationManager (how often there will be any update)
        const val SHORT_DISTANCE = 1f
        const val LONG_DISTANCE = 10f

        val dateFormatter = SimpleDateFormat("dd-MM-yyyy")
        val timeFormatter = SimpleDateFormat("HH:mm")
    }
}