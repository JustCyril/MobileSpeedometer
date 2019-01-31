package com.example.cyril.mobilespeedometer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.location.LocationManager
import com.example.cyril.mobilespeedometer.Listeners.GPSLocationListener
import com.example.cyril.mobilespeedometer.Model.Speed

class MainActivity : AppCompatActivity() {
    var speed : Speed? = null
    var displayedDate : TextView? = null
    var displayedTime : TextView? = null
    var displayedSpeed : TextView? = null
    var displayedCounter : TextView? = null
    var displayedGPSStatus : TextView? = null
    var btnReady : Button? = null
    var locationListener : GPSLocationListener? = null
    lateinit var presenter : MainPresenter
    private var locationManager : LocationManager? = null

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
        displayedTime = findViewById(R.id.textView_Time)
        displayedSpeed = findViewById(R.id.textView_CurrentSpeed)
        displayedCounter = findViewById(R.id.textView_TimerCounter)

        btnReady = findViewById(R.id.btn_ready_to_measure)
        btnReady?.setOnClickListener {readyToRace()}

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        presenter = MainPresenter(this)
        locationListener = GPSLocationListener(presenter)

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
        locationManager?.removeUpdates(locationListener)
    }

    fun changeGPSStatus(status : String) {
        displayedGPSStatus?.setText(status)
    }

    fun changeSpeed(speed : Int) {
        displayedSpeed?.setText(speed.toString())
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
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, interval, distance, locationListener)
        } catch (ex: SecurityException) {
            changeGPSStatus("SecurityException") //just for test-info, later will change this line
        }
    }

}
