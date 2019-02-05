package com.example.cyril.mobilespeedometer.Navi

interface IGPSObservable {
    fun registerObserver(o: IGPSObserver)
    fun removeObserver(o: IGPSObserver)
    fun notifyLocationChanged()
    fun notifyStatusChanged(status: String)
    fun notifyProviderChanged(providerStatus: String)
}