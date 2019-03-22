package com.example.cyril.mobilespeedometer.utils.navi

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.example.cyril.mobilespeedometer.main.MainPresenter
import com.example.cyril.mobilespeedometer.observers.GPSObservableContract

class GPSLocationListener: LocationListener, GPSObservableContract {

    var location = Location(LocationManager.GPS_PROVIDER)

    private var observer: MainPresenter? = null

    init {
        this.observer = observer
    }

    override fun registerObserver(o: MainPresenter) {
        observer = o
    }

    override fun removeObserver(o: MainPresenter) {
        observer = null
    }

    override fun onLocationChanged(loc: Location) {
        location = loc
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
            observer?.onLocationChange()
    }
    override fun notifyStatusChanged(status: String) {
            observer?.onGPSStatusChange(status)
    }
    override fun notifyProviderChanged(providerStatus: String) {
            observer?.onProviderStatusChange(providerStatus)
    }

}