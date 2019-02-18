package com.example.cyril.mobilespeedometer.DBHelper

import com.example.cyril.mobilespeedometer.MainPresenter

interface IDBObservable {
    fun registerObserver(o: MainPresenter)
    fun removeObserver(o: MainPresenter)
    fun notifyObservers()
}