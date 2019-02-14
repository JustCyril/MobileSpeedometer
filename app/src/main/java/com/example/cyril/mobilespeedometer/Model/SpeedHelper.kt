package com.example.cyril.mobilespeedometer.Model

import com.example.cyril.mobilespeedometer.MainPresenter

class SpeedHelper () : ISpeedObservable {

    private  var speed = Speed(0);
    private var observer: MainPresenter? = null

    init {
        this.observer = observer
    }

    override fun registerObserver(o: MainPresenter) {
        observer = o
    }

    override fun removeObserver(o: MainPresenter) {
        observer = null
    }

    override fun notifyObservers(newSpeed: Int) {
            observer?.onSpeedChange(newSpeed)
    }

    fun setSpeed(incomeSpeed: Int) {
        speed.currentValue = incomeSpeed
        notifyObservers(incomeSpeed)
    }
}