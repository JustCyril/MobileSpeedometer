package com.example.cyril.mobilespeedometer

import com.example.cyril.mobilespeedometer.Listeners.IObserver
import com.example.cyril.mobilespeedometer.Model.Speed
import java.util.*

class MainPresenter (private var activity: MainActivity) : IObserver {

    private var speed = Speed(0)

    init {
        this.speed = speed
        speed.registerObserver(this)
    }

    fun changeSpeed (incomeSpeed: Int) {
        speed.setSpeed(incomeSpeed*3600/1000)//speed is displayed in km/h
    }

    //я полагаю, проще конечно сразу в changeSpeed показать эти данные в активити, но в рамках
    //учебного процесса пока что разнесем это в разные методы
    //... да и вообще Speed должна быть "слушателем" навигации. Как только изменилось что-то, в ней меняются
    //данные, а она уже рассылает всем уведомления

    override fun onIntValueChange(value: Int) {
        activity.changeDisplayedSpeed(value)
    }

    fun changeGPSStatus (status : String) {
        activity.changeGPSStatus(status)
    }
}

