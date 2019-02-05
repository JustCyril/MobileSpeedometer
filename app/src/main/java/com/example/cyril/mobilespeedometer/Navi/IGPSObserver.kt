package com.example.cyril.mobilespeedometer.Navi


interface IGPSObserver {
    fun onLocationChange()

    fun onGPSStatusChange(status : String)

    fun onProviderStatusChange(status : String)
}