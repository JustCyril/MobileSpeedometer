package com.example.cyril.mobilespeedometer.Listeners

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import com.example.cyril.mobilespeedometer.MainActivity

class GPSLocationListener (private var activity: MainActivity) : LocationListener {

    lateinit var location: Location // location

    override fun onLocationChanged(location: Location) {
        val speed = (location.speed.toInt())*3600/1000 //in km/h
    }
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        activity.changeGPSStatus(status.toString())
        if (status == 2) { activity.changeGPSStatus("OK")}
    }
    override fun onProviderEnabled(provider: String) {activity.changeGPSStatus("Enabled")}
    override fun onProviderDisabled(provider: String) {activity.changeGPSStatus("Disabled")}
}