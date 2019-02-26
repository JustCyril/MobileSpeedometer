package com.example.cyril.mobilespeedometer.observers


interface IGPSObserver {
    fun onLocationChange()

    fun onGPSStatusChange(status : String)

    fun onProviderStatusChange(status : String)
}