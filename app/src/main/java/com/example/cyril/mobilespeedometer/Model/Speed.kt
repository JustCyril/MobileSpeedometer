package com.example.cyril.mobilespeedometer.Model

import com.example.cyril.mobilespeedometer.Listeners.SpeedChangeListener
import kotlin.properties.Delegates

class Speed (speed : Int, listener: SpeedChangeListener){
    var currentSpeed : Int by Delegates.observable(
        initialValue = 0,
        onChange = {prop, old, new -> listener.onValueChange(new)}
    )
}