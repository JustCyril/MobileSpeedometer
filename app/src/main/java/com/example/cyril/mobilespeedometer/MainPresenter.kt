package com.example.cyril.mobilespeedometer

import com.example.cyril.mobilespeedometer.Adapter.ListResultsAdapter
import com.example.cyril.mobilespeedometer.DBHelper.DBHelper
import com.example.cyril.mobilespeedometer.DBHelper.IDBObserver
import com.example.cyril.mobilespeedometer.Model.Result.Result
import com.example.cyril.mobilespeedometer.Navi.GPSLocationListener
import com.example.cyril.mobilespeedometer.Navi.IGPSObserver
import com.example.cyril.mobilespeedometer.Model.Speed.ISpeedObserver
import com.example.cyril.mobilespeedometer.Model.Speed.SpeedHelper
import java.text.DecimalFormat

class MainPresenter (private var activity: MainActivity) : ISpeedObserver, IGPSObserver, IDBObserver {

    var speedHelper = SpeedHelper()
    var locationListener = GPSLocationListener()
    var dbHelper = DBHelper(activity)
    var listResults: List<Result> = ArrayList<Result>()

    var readyToRace = false
    var inRace = false

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
        speedHelper.setSpeed(incomeSpeed*3600/1000)//speed is in km/h
    }

    //я полагаю, проще конечно сразу в changeSpeed показать эти данные в активити, но в рамках
    //учебного процесса пока что разнесем это в разные методы
    //... да и вообще Speed должна быть "слушателем" навигации. Как только изменилось что-то, в ней меняются
    //данные, а она уже рассылает всем уведомления

    fun readyToRace() {
        if (activity.readyToRace()) {
            readyToRace = true
            transformBtnReadyToStop()
        }
    }

    fun transformBtnReadyToStop() {
        activity.transformBtnReadyToStop()
    }

    fun stopRace() {
        StopTimer()
        initBtnReadyAgain()
    }

    override fun onSpeedChange(newSpeed: Int) {
        //we get speed in km/h from model because changeSpeed-fun above set speed after calculation

        activity.changeDisplayedSpeed(newSpeed)

        if (readyToRace) {
            if (newSpeed > 2) {
                inRace = true
                readyToRace = false
                StartTimer()
            }
        }
        if (inRace) {
            if (newSpeed > 100) {
                getResult()
                stopRace()
                inRace = false
            }
        }
    }

    fun changeCorrdinates(lat: Double, long: Double) {
        val formatter = DecimalFormat("0.####")
        activity.changeDisplayedCoordinates(formatter.format(lat), formatter.format(long))
    }

    fun StartTimer() {
        activity.StartTimer()
    }

    fun StopTimer() {
        activity.StopTimer()
    }

    fun saveResult (date: String, time: String, secs: Int, decisecs: Int) {
        val result = Result(0, date, time, "$secs : $decisecs")
        dbHelper.addResult(result)
    }

    fun initBtnReadyAgain() {
        activity.initBtnReady()
    }

    fun getResult() {
        activity.sendResult()
    }

    fun refreshListResult() {
        listResults = dbHelper.allResults
        //или в активити передавать listResults?... Но тогда она узнает о данных типа Result
        val adapter = ListResultsAdapter(activity, listResults)
        activity.refreshListResult(adapter)
    }

    override fun onDBUpdated() {
        refreshListResult()
    }


}

