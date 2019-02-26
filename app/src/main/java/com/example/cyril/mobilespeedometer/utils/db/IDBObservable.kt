package com.example.cyril.mobilespeedometer.utils.db

import com.example.cyril.mobilespeedometer.main.MainPresenter

interface IDBObservable {
    fun registerObserver(o: MainPresenter)
    fun removeObserver(o: MainPresenter)
    fun notifyObservers()
}