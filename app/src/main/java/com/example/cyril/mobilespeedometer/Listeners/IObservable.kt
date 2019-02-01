package com.example.cyril.mobilespeedometer.Listeners

interface IObservable {
    fun registerObserver(o: IObserver)
    fun removeObserver(o: IObserver)
    fun notifyObservers()
}