package com.example.cyril.mobilespeedometer

import com.example.cyril.mobilespeedometer.Listeners.GPSLocationListener
import com.example.cyril.mobilespeedometer.Listeners.IGPSObserver
import com.example.cyril.mobilespeedometer.Model.ISpeedObserver
import com.example.cyril.mobilespeedometer.Model.Speed
import java.util.*

class MainPresenter (private var activity: MainActivity) : ISpeedObserver, IGPSObserver {

    private var speed = Speed(0)
    open var locationListener = GPSLocationListener()

    init {
        this.speed = speed
        speed.registerObserver(this)

        this.locationListener = locationListener
        locationListener.registerObserver(this)
    }

    fun changeSpeed (incomeSpeed: Int) {
        speed.setSpeed(incomeSpeed*3600/1000)//speed is displayed in km/h
    }

    //я полагаю, проще конечно сразу в changeSpeed показать эти данные в активити, но в рамках
    //учебного процесса пока что разнесем это в разные методы
    //... да и вообще Speed должна быть "слушателем" навигации. Как только изменилось что-то, в ней меняются
    //данные, а она уже рассылает всем уведомления

    override fun onSpeedChange() {
        activity.changeDisplayedSpeed(speed.currentValue)
    }

    override fun onLocationChange() {
        changeSpeed(locationListener.location.speed.toInt())
    }

    override fun onGPSStatusChange(status : String) {
        activity.changeGPSStatus(status)
    }

    override fun onProviderStatusChange(status : String) {
        activity.changeGPSStatus(status)
    }
}

