package com.example.cyril.mobilespeedometer.Listeners

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import java.util.*

class GPSLocationListener : LocationListener, IGPSObservable {

    lateinit var location: Location

    private val observers: MutableList<IGPSObserver>

    init {
        observers = LinkedList()
    }

    override fun registerObserver(o: IGPSObserver) {
        observers.add(o)
    }

    override fun removeObserver(o: IGPSObserver) {
        observers.remove(o)
    }

    override fun onLocationChanged(location: Location) {
        notifyLocationChanged()
    }
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

        if (status == 2) { notifyStatusChanged("OK")
        } else {
            notifyStatusChanged(status.toString())
        }
    }
    override fun onProviderDisabled(provider: String) {notifyProviderChanged("Disabled")}
    override fun onProviderEnabled(provider: String) {notifyProviderChanged("Enabled")}


    override fun notifyLocationChanged() {
        for (observer in observers)
            observer.onLocationChange()
    }
    override fun notifyStatusChanged(status: String) {
        for (observer in observers)
            observer.onGPSStatusChange(status)
    }
    override fun notifyProviderChanged(providerStatus: String) {
        for (observer in observers)
            observer.onProviderStatusChange(providerStatus)
    }

}