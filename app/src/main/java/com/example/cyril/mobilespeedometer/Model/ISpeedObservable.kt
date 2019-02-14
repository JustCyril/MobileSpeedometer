package com.example.cyril.mobilespeedometer.Model

import com.example.cyril.mobilespeedometer.MainPresenter

interface ISpeedObservable {
    fun registerObserver(o: MainPresenter)
    fun removeObserver(o: MainPresenter)
    fun notifyObservers(newSpeed: Int)
}