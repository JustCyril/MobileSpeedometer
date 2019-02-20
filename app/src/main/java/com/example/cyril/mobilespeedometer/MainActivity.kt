package com.example.cyril.mobilespeedometer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.location.LocationManager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.*
import com.example.cyril.mobilespeedometer.Adapter.RecViewResultsAdapter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    var txtvw_Date : TextView? = null
    //var displayedTime : TextView? = null
    var txtvw_Speed : TextView? = null
    var txtvw_CounterSeconds : TextView? = null
    var txtvw_CounterMillis : TextView? = null
    var txtvw_GPSStatus : TextView? = null

/*    var displayedResultDate : TextView? = null
    var displayedResultTime : TextView? = null
    var displayedResult : TextView? = null*/
    lateinit var recview_Results : RecyclerView

    var btnReady : Button? = null

/*    var chrono: Chronometer? = null
    var btnStart : Button? = null
    var btnStop: Button? = null*/

    var timer: Timer? = null
    var isTimerRunning = false
    var centisecs = 0
    var decisecs = 0 //tried to use millis, but there was a huge gape between real time and showed timer (suppose, because of frequent textView redrawing)
    var secs = 0

    var txtvw_Latitude : TextView? = null
    var txtvw_Longitude : TextView? = null

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

        txtvw_GPSStatus = findViewById(R.id.textView_GPS_status)
        txtvw_Date = findViewById(R.id.textView_Date)
        setCurrentDate()
        //displayedTime = findViewById(R.id.textView_time)
        //setCurrentTime()

        txtvw_Speed = findViewById(R.id.textView_CurrentSpeed)
        txtvw_CounterSeconds = findViewById(R.id.textView_TimerCounter_seconds)
        txtvw_CounterMillis = findViewById(R.id.textView_TimerCounter_millis)

/*        displayedResultDate = findViewById(R.id.textView_date_result)
        displayedResultTime = findViewById(R.id.textView_time_result)
        displayedResult = findViewById(R.id.textView_timer_result)*/

        recview_Results = findViewById(R.id.recview_results)
        recview_Results.layoutManager = LinearLayoutManager(this)

        txtvw_Latitude = findViewById(R.id.textView_GPS_latitude)
        txtvw_Longitude = findViewById(R.id.textView_GPS_longitude)

/*        btnStart = findViewById(R.id.btn_Start)
        btnStart?.setOnClickListener { onStartClick() }
        btnStop = findViewById(R.id.btn_Stop)
        btnStop?.setOnClickListener { onStopClick() }
        chrono = findViewById(R.id.chronometer)*/

        btnReady = findViewById(R.id.btn_ready_to_measure)
        initBtnReady()


        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        presenter = MainPresenter(this)
        presenter.refreshListResult()

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
        txtvw_GPSStatus?.setText(status)
    }

    fun changeDisplayedSpeed(speed : Int) {
        txtvw_Speed?.setText(speed.toString())
    }

    fun setCurrentDate(){
        txtvw_Date?.setText(dateFormatter.format(Date()))
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
       if (txtvw_Speed?.text.toString().toInt() > 2) {
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
        txtvw_Latitude?.setText(lat)
        txtvw_Longitude?.setText(long)
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
        txtvw_CounterMillis?.setText(decisecs.toString())
        txtvw_CounterSeconds?.setText(secs.toString())
    }

    fun StartTimer() {
        isTimerRunning = true

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
        isTimerRunning = false
    }

    fun sendResult() {
        val date = dateFormatter.format(Date())
        val time = timeFormatter.format(Date())
        presenter.saveResult(date, time, secs, centisecs)
    }

    fun onTimerTick(){
        //there is so huge gape between real timer and this logic :(
        //first idea: use timer period 100, not 10. And work without centisecs. But time accuracy is low :(
        centisecs++
        if (centisecs%10 == 0) {
            decisecs = centisecs/10
            if (decisecs == 10) {
                secs++
                txtvw_CounterSeconds?.setText(secs.toString())
                decisecs = 0
                centisecs = 0
            }
            txtvw_CounterMillis?.setText(decisecs.toString())
        }
    }

/*    fun showResult (result: String) {
        displayedResult?.setText(result)
        displayedResultDate?.setText(dateFormatter.format(Date()))
        displayedResultTime?.setText(timeFormatter.format(Date()))
    }*/

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

    fun refreshListResult(adapter: RecViewResultsAdapter) {
        recview_Results?.adapter = adapter
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putInt("centisecs", centisecs)
        outState?.putInt("secs", secs)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
    }

}
