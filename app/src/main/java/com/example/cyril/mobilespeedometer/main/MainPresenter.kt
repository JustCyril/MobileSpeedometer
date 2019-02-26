package com.example.cyril.mobilespeedometer.main

import com.example.cyril.mobilespeedometer.adapter.RecViewResultsAdapter
import com.example.cyril.mobilespeedometer.utils.db.DBHelper
import com.example.cyril.mobilespeedometer.utils.db.IDBObserver
import com.example.cyril.mobilespeedometer.model.Result
import com.example.cyril.mobilespeedometer.utils.navi.GPSLocationListener
import com.example.cyril.mobilespeedometer.observers.IGPSObserver
import com.example.cyril.mobilespeedometer.observers.ISpeedObserver
import com.example.cyril.mobilespeedometer.utils.SpeedHelper
import java.text.DecimalFormat

class MainPresenter (private var activity: MainActivity) : ISpeedObserver,
    IGPSObserver, IDBObserver {

    private var speedHelper = SpeedHelper()
    var locationListener = GPSLocationListener() //not private because of activity location updates request
    private var dbHelper = DBHelper(activity)
    //private var listResults: List<Result> = ArrayList<Result>()

    private var readyToRace = false
    private var inRace = false

    init {
        this.speedHelper = speedHelper
        this.locationListener = locationListener
        regAsObserver()
    }

    private fun regAsObserver() {
        speedHelper.registerObserver(this)
        locationListener.registerObserver(this)
        dbHelper.registerObserver(this)
    }

    // ---- Observer-actions --------

    override fun onLocationChange() {
        changeSpeed(locationListener.location.speed.toInt())
        changeCorrdinates(locationListener.location.latitude, locationListener.location.longitude)
    }

    override fun onGPSStatusChange(status : String) {
        activity.changeGPSStatus(status)
    }

    override fun onProviderStatusChange(status : String) {
        //there is some problem: when phone is rotating, provider status become "disabled",
        //but everything is still working. So, just skip this action until problem will be solved
        //activity.changeGPSStatus(status)
    }

    private fun changeSpeed (incomeSpeed: Int) {
        speedHelper.setSpeed(incomeSpeed*3600/1000)//speed is in km/h
    }

    //я полагаю, проще конечно сразу в changeSpeed показать эти данные в активити, но в рамках
    //учебного процесса пока что разнесем это в разные методы
    //... да и вообще Speed должна быть "слушателем" навигации. Как только изменилось что-то, в ней меняются
    //данные, а она уже рассылает всем уведомления

    override fun onSpeedChange(newSpeed: Int) {
        //we get speed in km/h from model because changeSpeed-fun above set speed after calculation

        activity.changeDisplayedSpeed(newSpeed)

        if (readyToRace) {
            if (newSpeed > 1) {
                inRace = true
                readyToRace = false
                StartTimer()
            }
        }
        if (inRace) {
            if (newSpeed >= 100) {
                getResult()
                stopRace()
                inRace = false
            }
        }
    }

    private fun changeCorrdinates(lat: Double, long: Double) {
        val formatter = DecimalFormat("0.####")
        activity.changeDisplayedCoordinates(formatter.format(lat), formatter.format(long))
    }

    // ---- VIEW and its events -----

    fun initCommonView() {
        activity.initCommonView()
    }

    fun initRaceView() {
        activity.initRaceView()
    }

    fun onBtnReadyClick() {
        readyToRace()
    }

    fun onBtnStopClick() {
        stopRace()
    }

    fun continueRace() {
        initTimer()
        StartTimer()
        inRace = true
    }

    // ---- RACE LOGIC --------

    private fun readyToRace() {
        if (activity.readyToRace()) {
            readyToRace = true
            initRaceView()
        }
    }

    private fun stopRace() {
        StopTimer()
        initCommonView()
    }

    fun initTimer() {
        activity.initTimer()
    }

    fun StartTimer() {
        activity.StartTimer()
    }

    fun StopTimer() {
        activity.StopTimer()
    }

    // ---- DB and result saving --------

    fun getResult() {
        activity.sendResult()
    }

    fun saveResult (date: String, time: String, secs: Int, centisecs: Int) {
        var result = Result()
        if (centisecs<=9) {
            val cent = "0$centisecs"
            result = Result(0, date, time, "$secs : $cent")
        } else { result = Result(0, date, time, "$secs : $centisecs")
        }
        dbHelper.addResult(result)
    }

    override fun onDBUpdated() {
        refreshListResult()
    }

    fun refreshListResult() {
        val listResults = dbHelper.getAll()
        //или в активити передавать listResults?... Но тогда она узнает о данных типа Result
        val adapter = RecViewResultsAdapter(listResults)
        activity.refreshListResult(adapter)
    }

}

