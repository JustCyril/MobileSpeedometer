package com.example.cyril.mobilespeedometer.Model

import com.example.cyril.mobilespeedometer.Listeners.IObservable
import com.example.cyril.mobilespeedometer.Listeners.IObserver
import java.util.*

class Speed (var currentValue: Int) : IObservable {

    private val observers: MutableList<IObserver>

    init {
        observers = LinkedList()
    }

    override fun registerObserver(o: IObserver) {
        observers.add(o)
    }

    override fun removeObserver(o: IObserver) {
        observers.remove(o)
    }

    override fun notifyObservers() {
        for (observer in observers)
            observer.onIntValueChange(currentValue)
    }

    fun setSpeed(speed: Int) {
        this.currentValue = speed
        notifyObservers()
    }
}


