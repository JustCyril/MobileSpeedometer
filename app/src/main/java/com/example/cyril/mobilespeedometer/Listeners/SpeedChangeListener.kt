package com.example.cyril.mobilespeedometer.Listeners

import com.example.cyril.mobilespeedometer.MainActivity

class SpeedChangeListener (private var activity: MainActivity) {
    fun onValueChange(newValue : Int) = newValue
}