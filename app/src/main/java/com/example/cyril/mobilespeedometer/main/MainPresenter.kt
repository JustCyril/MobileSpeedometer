package com.example.cyril.mobilespeedometer.main

import android.widget.Toast
import com.example.cyril.mobilespeedometer.adapter.RecViewResultsAdapter
import com.example.cyril.mobilespeedometer.model.Result
import com.example.cyril.mobilespeedometer.model.repository.Repository
import com.example.cyril.mobilespeedometer.utils.navi.GPSLocationListener
import com.example.cyril.mobilespeedometer.observers.GPSObserverContract
import com.example.cyril.mobilespeedometer.utils.SpeedHelper
import java.text.DecimalFormat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainPresenter (private var activity: MainActivity) : GPSObserverContract {

    private var speedHelper = SpeedHelper()
    var locationListener = GPSLocationListener() //not private because of activity location updates request
    //private var listResults: List<Result> = ArrayList<Result>()

    private var readyToRace = false
    private var inRace = false

    init {
        this.speedHelper = speedHelper
        this.locationListener = locationListener
        regAsObserver()
    }

    private fun regAsObserver() {
        locationListener.registerObserver(this)
    }

    // ---- Observer-actions --------

    override fun onLocationChange() {
        changeSpeed(locationListener.location.speed.toInt())
        changeCoordinates(locationListener.location.latitude, locationListener.location.longitude)
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

        val speed = (incomeSpeed*3600/1000).toInt() //speed is in km/h

        speedHelper.setSpeed(speed)
        activity.changeDisplayedSpeed(speed)

        if (readyToRace) {
            if (speed > 1) {
                inRace = true
                readyToRace = false
                StartTimer()
            }
        }
        if (inRace) {
            if (speed >= 100) {
                getResult()
                stopRace()
                inRace = false
            }
        }
    }

    private fun changeCoordinates(lat: Double, long: Double) {
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

        Repository(activity).addResult(result)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    if (!it) {
                        Toast.makeText(activity, "Ошибка сохранения данных!", Toast.LENGTH_LONG).show()
                    }
                }
            )

        refreshListResult()
    }

    fun refreshListResult() {

        Repository(activity).getAllResult()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    activity.refreshListResult(RecViewResultsAdapter(it, {item -> deleteResult(item)}))
                },
                {
                    Toast.makeText(activity, "Ошибка отображения данных!", Toast.LENGTH_LONG).show()
                }
            )


    }

    fun deleteResult(result: Result) {
        //delete result from recycler view and from db
        Repository(activity).deleteResult(result)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (!it) {
                        Toast.makeText(activity, "Ошибка удаления данных!", Toast.LENGTH_LONG).show()
                    }
                }
            )

    }

}

