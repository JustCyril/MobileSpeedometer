package com.example.cyril.mobilespeedometer.Model

interface ISpeedObservable {
    fun registerObserver(o: ISpeedObserver)
    fun removeObserver(o: ISpeedObserver)
    fun notifyObservers()
}