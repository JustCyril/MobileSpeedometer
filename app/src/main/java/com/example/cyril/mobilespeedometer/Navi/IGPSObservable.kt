package com.example.cyril.mobilespeedometer.Navi

import com.example.cyril.mobilespeedometer.MainPresenter

interface IGPSObservable {
    fun registerObserver(o: MainPresenter)
    fun removeObserver(o: MainPresenter)
    fun notifyLocationChanged()
    fun notifyStatusChanged(status: String)
    fun notifyProviderChanged(providerStatus: String)
}