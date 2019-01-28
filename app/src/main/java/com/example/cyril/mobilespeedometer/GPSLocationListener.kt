package com.example.cyril.mobilespeedometer

import android.location.Location
import android.location.LocationListener
import android.os.Bundle

class GPSLocationListener (private var activity: MainActivity) : LocationListener {
    override fun onLocationChanged(location: Location) {
        val speed = location.speed.toInt()
        //this leads to app crash
        //activity.changeSpeed(speed)
    }
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {activity.changeGPSStatus(status.toString())}
    override fun onProviderEnabled(provider: String) {activity.changeGPSStatus("Enabled")}
    override fun onProviderDisabled(provider: String) {activity.changeGPSStatus("Disabled")}
}