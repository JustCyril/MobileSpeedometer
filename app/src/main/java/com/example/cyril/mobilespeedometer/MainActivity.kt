package com.example.cyril.mobilespeedometer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.util.*

class MainActivity : AppCompatActivity() {

    var displayedDate : TextView? = null
    var displayedTime : TextView? = null
    var displayedSpeed : TextView? = null
    var displayedCounter : TextView? = null
    var btnReady : Button? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayedDate = findViewById(R.id.textView_Date)
        displayedTime = findViewById(R.id.textView_Time)
        displayedSpeed = findViewById(R.id.textView_CurrentSpeed)
        displayedCounter = findViewById(R.id.textView_TimerCounter)
        btnReady = findViewById(R.id.btn_ready_to_measure)

        setTime()

    }

    fun setTime() {
        //фигня, коряво работает
        var date = Calendar.getInstance().getTime().toString()
        displayedDate?.setText(date)

    }
}
