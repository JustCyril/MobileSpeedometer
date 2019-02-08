package com.example.cyril.mobilespeedometer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.location.LocationManager
import android.os.Handler
import android.os.SystemClock
import android.widget.Chronometer
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    var displayedDate : TextView? = null
    var displayedTime : TextView? = null
    var displayedSpeed : TextView? = null
    var displayedCounter : TextView? = null
    var displayedGPSStatus : TextView? = null
    var btnReady : Button? = null

    var chrono: Chronometer? = null
    var btnStart : Button? = null
    var btnStop: Button? = null
    var handler: Handler? = null

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
        displayedCounter = findViewById(R.id.textView_TimerCounter)

        displayedLatitude = findViewById(R.id.textView_GPS_latitude)
        displayedLongitude = findViewById(R.id.textView_GPS_longitude)

        btnStart = findViewById(R.id.btn_Start)
        btnStart?.setOnClickListener { onStartClick() }
        btnStop = findViewById(R.id.btn_Stop)
        btnStop?.setOnClickListener { onStopClick() }
        chrono = findViewById(R.id.chronometer)

        handler = Handler()

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
    }

    fun setCurrentDate(){
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        displayedDate?.setText(formatter.format(Date()))
    }

    private fun readyToRace() {

        //if car is moving, user will receive a message
/*        if (speed?.currentSpeed > 2) {
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
        }*/

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
    }

    fun onStopClick() {
        chrono?.stop()
    }

}
