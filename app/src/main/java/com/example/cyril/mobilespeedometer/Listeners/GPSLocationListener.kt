package com.example.cyril.mobilespeedometer.Listeners

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import com.example.cyril.mobilespeedometer.MainPresenter

class GPSLocationListener (private var presenter: MainPresenter) : LocationListener {

    lateinit var location: Location

    override fun onLocationChanged(location: Location) {
        presenter.changeSpeed(location.speed.toInt())
    }
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

        if (status == 2) { presenter.changeGPSStatus("OK")
        } else {
            presenter.changeGPSStatus(status.toString())
        }
    }
    override fun onProviderDisabled(provider: String) {presenter.changeGPSStatus("Disabled")}
    override fun onProviderEnabled(provider: String) {presenter.changeGPSStatus("Enabled")}
}