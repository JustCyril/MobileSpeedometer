package com.example.cyril.mobilespeedometer.observers


interface GPSObserverContract {
    fun onLocationChange()

    fun onGPSStatusChange(status : String)

    fun onProviderStatusChange(status : String)
}