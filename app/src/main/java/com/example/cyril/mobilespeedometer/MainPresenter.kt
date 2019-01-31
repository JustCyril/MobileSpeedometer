package com.example.cyril.mobilespeedometer

class MainPresenter (private var activity: MainActivity) {
    fun changeSpeed (speed: Int) {
        activity.changeSpeed(speed)
    }

    fun changeGPSStatus (status : String) {
        activity.changeGPSStatus(status)
    }
}