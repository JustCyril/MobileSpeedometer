package com.example.cyril.mobilespeedometer.Listeners


interface IGPSObserver {
    fun onLocationChange()

    fun onGPSStatusChange(status : String)

    fun onProviderStatusChange(status : String)
}