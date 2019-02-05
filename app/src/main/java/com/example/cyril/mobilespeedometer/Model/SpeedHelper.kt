package com.example.cyril.mobilespeedometer.Model

import java.util.*

class SpeedHelper () : ISpeedObservable {

    private  var speed = Speed(0);
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

    override fun notifyObservers(newSpeed: Int) {
        for (observer in observers)
            observer.onSpeedChange(newSpeed)
    }

    fun setSpeed(incomeSpeed: Int) {
        speed.currentValue = incomeSpeed
        notifyObservers(incomeSpeed)
    }
}