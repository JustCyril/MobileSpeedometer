package com.example.cyril.mobilespeedometer.utils

import com.example.cyril.mobilespeedometer.model.Speed

class SpeedHelper {

    private var speed = Speed(0);

    fun setSpeed(incomeSpeed: Int) {
        speed.currentValue = incomeSpeed
    }
}