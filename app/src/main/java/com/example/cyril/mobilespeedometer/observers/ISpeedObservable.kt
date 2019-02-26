package com.example.cyril.mobilespeedometer.observers

import com.example.cyril.mobilespeedometer.main.MainPresenter

interface ISpeedObservable {
    fun registerObserver(o: MainPresenter)
    fun removeObserver(o: MainPresenter)
    fun notifyObservers(newSpeed: Int)
}