package com.example.cyril.mobilespeedometer

import android.location.Location
import android.location.LocationListener
import android.os.Bundle

class GPSLocationListener (private var activity: MainActivity) : LocationListener {
    override fun onLocationChanged(location: Location) {
        activity.changeSpeed(location.speed.toInt())
    }
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {activity.changeGPSStatus(status.toString())}
    override fun onProviderEnabled(provider: String) {activity.changeGPSStatus("Launching")}
    override fun onProviderDisabled(provider: String) {activity.changeGPSStatus("Disabled")}
}