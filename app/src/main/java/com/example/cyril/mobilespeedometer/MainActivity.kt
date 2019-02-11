package com.example.cyril.mobilespeedometer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.location.LocationManager
import android.os.SystemClock
import android.support.v7.app.AlertDialog
import android.widget.Chronometer
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    var displayedDate : TextView? = null
    var displayedTime : TextView? = null
    var displayedSpeed : TextView? = null
    var displayedCounterSeconds : TextView? = null
    var displayedCounterMillis : TextView? = null
    var displayedGPSStatus : TextView? = null
    var btnReady : Button? = null
    var readyToRace : Boolean = false
    var inRace : Boolean = false

    var chrono: Chronometer? = null
    var btnStart : Button? = null
    var btnStop: Button? = null

    val timer = Timer()
    var millis = 0
    var secs = 0

    var displayedLatitude : TextView? = null
    var displayedLongitude : TextView? = null

    lateinit var presenter : MainPresenter
    var locationManager : LocationManager? = null

    //update-time for locationManager (how often there will be any update)
    private val FAST_INTERVAL = 10L
    private val SLOW_INTERVAL = 1000L

    //update-distance for locationManager (how often there will be any update)
    private val SHORT_DISTANCE = 1f
    private val LONG_DISTANCE = 10f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayedGPSStatus = findViewById(R.id.textView_GPS_status)
        displayedDate = findViewById(R.id.textView_Date)
        setCurrentDate()
        displayedTime = findViewById(R.id.textView_Time)
        displayedSpeed = findViewById(R.id.textView_CurrentSpeed)
        displayedCounterSeconds = findViewById(R.id.textView_TimerCounter_seconds)
        displayedCounterMillis = findViewById(R.id.textView_TimerCounter_millis)

        displayedLatitude = findViewById(R.id.textView_GPS_latitude)
        displayedLongitude = findViewById(R.id.textView_GPS_longitude)

        btnStart = findViewById(R.id.btn_Start)
        btnStart?.setOnClickListener { onStartClick() }
        btnStop = findViewById(R.id.btn_Stop)
        btnStop?.setOnClickListener { onStopClick() }
        chrono = findViewById(R.id.chronometer)

        btnReady = findViewById(R.id.btn_ready_to_measure)
        btnReady?.setOnClickListener {readyToRace()}

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        presenter = MainPresenter(this)

        if (UtilsPermissions(this).checkSelfPermission(this)) {
            employLocationManager(SLOW_INTERVAL, LONG_DISTANCE)
        }

    }

    override fun onResume(){
        super.onResume()
        employLocationManager(SLOW_INTERVAL, LONG_DISTANCE)
    }

    override fun onPause() {
        super.onPause()
        locationManager?.removeUpdates(presenter.locationListener)
    }

    fun changeGPSStatus(status : String) {
        displayedGPSStatus?.setText(status)
    }

    fun changeDisplayedSpeed(speed : Int) {
        displayedSpeed?.setText(speed.toString())
/*
        if (readyToRace) {
            if (speed > 1) {
                presenter.startTimer()
                inRace = true
            }
        }*/
    }

    fun setCurrentDate(){
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        displayedDate?.setText(formatter.format(Date()))
    }

    private fun readyToRace() {

        //if car is moving, user will receive a message
       if (displayedSpeed?.text.toString().toInt() > 2) {
            AlertDialog.Builder(this)
                .setTitle("Ошибка!")
                .setMessage("Автомобиль в движении! Пожалуйста, остановитесь полностью!")
                .setPositiveButton("Я всё понял!") { dialog, which ->
                    Toast.makeText(this, "Умница", Toast.LENGTH_SHORT).show()
                }
                .create()
                .show()

        } else {
            employLocationManager(FAST_INTERVAL, SHORT_DISTANCE)
            readyToRace = true
        }

    }

    fun employLocationManager(interval : Long, distance : Float) {
        try {
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, interval, distance, presenter.locationListener)
        } catch (ex: SecurityException) {
            changeGPSStatus("SecurityException") //just for test-info, later will change this line
        }
    }

    fun changeDisplayedCoordinates (lat: String, long: String) {
        displayedLatitude?.setText(lat)
        displayedLongitude?.setText(long)
    }

    fun onStartClick() {
        chrono?.setBase(SystemClock.elapsedRealtime())
        chrono?.start()
        presenter.StartTimer()
    }

    fun onStopClick() {
        chrono?.stop()
        presenter.StopTimer()
    }

    fun StartTimer() {
        displayedCounterMillis?.setText(millis.toString())
        displayedCounterSeconds?.setText(secs.toString())
        //если эту строчку расскомментить, то вылетает сразу же
        //timer.cancel()
        timer.schedule(0, 1) {onTimerTick()}
    }

    fun StopTimer() {
        timer.cancel()
        millis = 0
        secs = 0
    }

    fun onTimerTick() {
        millis = millis + 1
        //displayedCounterSeconds?.setText(millis.toString())

        if ((millis%100) == 0) {
            displayedCounterSeconds?.setText((millis / 100).toString())
        }
        displayedCounterMillis?.setText(millis.toString())
        if ((millis%1000) == 0) {
            secs = secs + 1
            displayedCounterSeconds?.setText(secs.toString())
        }

    }

}
