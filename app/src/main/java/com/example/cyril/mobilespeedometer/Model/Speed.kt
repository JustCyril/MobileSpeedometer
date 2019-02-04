package com.example.cyril.mobilespeedometer.Model

import com.example.cyril.mobilespeedometer.Model.ISpeedObservable
import com.example.cyril.mobilespeedometer.Model.ISpeedObserver
import java.util.*

class Speed (var currentValue: Int) : ISpeedObservable {

    private val observers: MutableList<ISpeedObserver>

    init {
        observers = LinkedList()
    }

    override fun registerObserver(o: ISpeedObserver) {
        observers.add(o)
    }

    override fun removeObserver(o: ISpeedObserver) {
        observers.remove(o)
    }

    override fun notifyObservers() {
        for (observer in observers)
            observer.onSpeedChange()
    }

    fun setSpeed(speed: Int) {
        this.currentValue = speed
        notifyObservers()
    }
}


