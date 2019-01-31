package com.example.cyril.mobilespeedometer.Model

import com.example.cyril.mobilespeedometer.MainPresenter
import kotlin.properties.Delegates

class Speed (private var speed : Int, private var presenter: MainPresenter){
    var currentSpeed : Int by Delegates.observable(speed){ prop, old, new ->
        presenter.changeSpeed(new)
        speed = new
    }
}