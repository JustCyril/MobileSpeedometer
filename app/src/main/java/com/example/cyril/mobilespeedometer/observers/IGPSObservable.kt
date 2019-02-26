package com.example.cyril.mobilespeedometer.observers

import com.example.cyril.mobilespeedometer.main.MainPresenter

interface IGPSObservable {
    fun registerObserver(o: MainPresenter)
    fun removeObserver(o: MainPresenter)
    fun notifyLocationChanged()
    fun notifyStatusChanged(status: String)
    fun notifyProviderChanged(providerStatus: String)
}