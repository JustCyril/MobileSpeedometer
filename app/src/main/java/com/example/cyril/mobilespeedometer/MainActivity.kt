package com.example.cyril.mobilespeedometer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.location.LocationManager
import android.os.SystemClock
import android.support.v7.app.AlertDialog
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.truncate

class MainActivity : AppCompatActivity() {
    var displayedDate : TextView? = null
    //var displayedTime : TextView? = null
    var displayedSpeed : TextView? = null
    var displayedCounterSeconds : TextView? = null
    var displayedCounterMillis : TextView? = null
    var displayedGPSStatus : TextView? = null
    var displayedResultDate : TextView? = null
    var displayedResultTime : TextView? = null
    var displayedResult : TextView? = null
    var btnReady : Button? = null

    var chrono: Chronometer? = null
    var btnStart : Button? = null
    var btnStop: Button? = null

    var timer: Timer? = null
    var centisecs = 0
    var decisecs = 0 //tried to use millis, but there was a huge gape between real time and showed timer (suppose, because of frequent textView redrawing)
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

    val dateFormatter = SimpleDateFormat("dd-MM-yyyy")
    val timeFormatter = SimpleDateFormat("HH:mm")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayedGPSStatus = findViewById(R.id.textView_GPS_status)
        displayedDate = findViewById(R.id.textView_Date)
        setCurrentDate()
        //displayedTime = findViewById(R.id.textView_time)
        //setCurrentTime()
        displayedSpeed = findViewById(R.id.textView_CurrentSpeed)
        displayedCounterSeconds = findViewById(R.id.textView_TimerCounter_seconds)
        displayedCounterMillis = findViewById(R.id.textView_TimerCounter_millis)

        displayedResultDate = findViewById(R.id.textView_date_result)
        displayedResultTime = findViewById(R.id.textView_time_result)
        displayedResult = findViewById(R.id.textView_timer_result)

        displayedLatitude = findViewById(R.id.textView_GPS_latitude)
        displayedLongitude = findViewById(R.id.textView_GPS_longitude)

/*        btnStart = findViewById(R.id.btn_Start)
        btnStart?.setOnClickListener { onStartClick() }
        btnStop = findViewById(R.id.btn_Stop)
        btnStop?.setOnClickListener { onStopClick() }
        chrono = findViewById(R.id.chronometer)*/

        btnReady = findViewById(R.id.btn_ready_to_measure)
        initBtnReady()


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
        displayedDate?.setText(dateFormatter.format(Date()))
    }

/*    fun setCurrentTime() {
        //there will be smt to set real-time in app. Don`t know what it will be yet.
    }*/

    fun onReadyToRaceClick() {
        presenter.readyToRace()
    }

    fun readyToRace(): Boolean {
        //if car is moving, user will receive a warning message
        //and var readyToRace in the presenter will not be changed
       if (displayedSpeed?.text.toString().toInt() > 2) {
            AlertDialog.Builder(this)
                .setTitle("Ошибка!")
                .setMessage("Автомобиль в движении! Пожалуйста, остановитесь полностью!")
                .setPositiveButton("Я всё понял!") { dialog, which ->
                    Toast.makeText(this, "Умница!", Toast.LENGTH_SHORT).show()
                }
                .create()
                .show()
           return false

        } else {
            employLocationManager(FAST_INTERVAL, SHORT_DISTANCE)
            initTimer()
           return true
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

/*    fun onStartClick() {
        chrono?.setBase(SystemClock.elapsedRealtime())
        chrono?.start()
        presenter.StartTimer()
    }

    fun onStopClick() {
        chrono?.stop()
        presenter.StopTimer()
    }*/

    fun initTimer() {
        timer = Timer() //возможно это костыль, но я не понял, как грамотно остановить таймер, инициализированный ранее. cancel() убивал его, schedule({cancel()}) тоже убивал... иных методов при гуглеже не нашел
        displayedCounterMillis?.setText(decisecs.toString())
        displayedCounterSeconds?.setText(secs.toString())
    }

    fun StartTimer() {

        timer?.schedule(0, 10) {
            runOnUiThread({
                    onTimerTick()
            })
        }
    }

    fun StopTimer() {
        timer?.cancel()
        timer?.purge()
        centisecs = 0
        decisecs = 0
        secs = 0
    }

    fun sendResult() {
        presenter.saveResult(secs, centisecs)
    }

    fun onTimerTick(){
        //there is so huge mistake between real timer and this logic :(
        //first idea: use timer period 100, not 10. And work without centisecs. But accuracy is low :(
        centisecs++
        if (centisecs%10 == 0) {
            decisecs = centisecs/10
            if (decisecs == 10) {
                secs++
                displayedCounterSeconds?.setText(secs.toString())
                decisecs = 0
                centisecs = 0
            }
            displayedCounterMillis?.setText(decisecs.toString())
        }
    }

    fun showResult (result: String) {
        displayedResult?.setText(result)
        displayedResultDate?.setText(dateFormatter.format(Date()))
        displayedResultTime?.setText(timeFormatter.format(Date()))
    }

    fun initBtnReady() {
        btnReady?.text = getString(R.string.btn_ready_to_race)
        btnReady?.setOnClickListener {onReadyToRaceClick()}
    }

    fun transformBtnReadyToStop() {
        btnReady?.text = getString(R.string.btn_stop_race)
        btnReady?.setOnClickListener {onStopRaceClick()}
    }

    fun onStopRaceClick() {
        presenter.stopRace()
    }

}
