package com.example.cyril.mobilespeedometer

import android.location.LocationManager
import com.example.cyril.mobilespeedometer.Navi.GPSLocationListener
import com.example.cyril.mobilespeedometer.Navi.IGPSObserver
import com.example.cyril.mobilespeedometer.Model.ISpeedObserver
import com.example.cyril.mobilespeedometer.Model.SpeedHelper
import java.text.DecimalFormat

class MainPresenter (private var activity: MainActivity) : ISpeedObserver, IGPSObserver {

    private var speedHelper = SpeedHelper()
    var locationListener = GPSLocationListener()

    init {
        this.speedHelper = speedHelper
        speedHelper.registerObserver(this)

        this.locationListener = locationListener
        locationListener.registerObserver(this)
    }
    override fun onLocationChange() {
        changeSpeed(locationListener.location.speed.toInt())
        changeCorrdinates(locationListener.location.latitude, locationListener.location.longitude)
    }

    override fun onGPSStatusChange(status : String) {
        activity.changeGPSStatus(status)
    }

    override fun onProviderStatusChange(status : String) {
        activity.changeGPSStatus(status)
    }

    fun changeSpeed (incomeSpeed: Int) {
        speedHelper.setSpeed(incomeSpeed*3600/1000)//speed is displayed in km/h
    }

    //я полагаю, проще конечно сразу в changeSpeed показать эти данные в активити, но в рамках
    //учебного процесса пока что разнесем это в разные методы
    //... да и вообще Speed должна быть "слушателем" навигации. Как только изменилось что-то, в ней меняются
    //данные, а она уже рассылает всем уведомления

    override fun onSpeedChange(newSpeed: Int) {
        activity.changeDisplayedSpeed(newSpeed)
    }

    fun changeCorrdinates(lat: Double, long: Double) {
        val formatter = DecimalFormat("0.####")
        activity.changeDisplayedCoordinates(formatter.format(lat), formatter.format(long))
    }
}

