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

    //---- VIEW ------
    private var txtvw_Date : TextView? = null
    private var txtvw_Speed : TextView? = null
    private var txtvw_CounterSeconds : TextView? = null
    private var txtvw_CounterMillis : TextView? = null
    private var txtvw_GPSStatus : TextView? = null

    lateinit private var recview_Results : RecyclerView

    private var btnReady : Button? = null //this btn will transform to StopBtn after click, see methods below

    private var txtvw_Latitude : TextView? = null
    private var txtvw_Longitude : TextView? = null

    //---- RACE LOGIC ------
    private var timer: Timer? = null
    private var isTimerRunning = false

    //tried to use millis, but there was a huge gape between real time and showed timer (suppose, because of frequent textView redrawing
    private var centisecs = 0
    private var decisecs = 0
    private var secs = 0

    //update-time for locationManager (how often there will be any update)
    private val FAST_INTERVAL = 10L
    private val SLOW_INTERVAL = 1000L

    //update-distance for locationManager (how often there will be any update)
    private val SHORT_DISTANCE = 1f
    private val LONG_DISTANCE = 10f

    val dateFormatter = SimpleDateFormat("dd-MM-yyyy")
    val timeFormatter = SimpleDateFormat("HH:mm")

    //---- Presenter and helpers ----

    lateinit private var presenter : MainPresenter
    private var locationManager : LocationManager? = null


    /*----------- LEGACY-vars ---------------------------------
        var displayedTime : TextView? = null
        var displayedResultDate : TextView? = null
        var displayedResultTime : TextView? = null
        var displayedResult : TextView? = null

        var chrono: Chronometer? = null
        var btnStart : Button? = null
        var btnStop: Button? = null
    --------------------------------------------------------*/



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        txtvw_GPSStatus = findViewById(R.id.textView_GPS_status)
        txtvw_Date = findViewById(R.id.textView_Date)
        setCurrentDate()

        txtvw_Speed = findViewById(R.id.textView_CurrentSpeed)
        txtvw_CounterSeconds = findViewById(R.id.textView_TimerCounter_seconds)
        txtvw_CounterMillis = findViewById(R.id.textView_TimerCounter_millis)

        recview_Results = findViewById(R.id.recview_results)
        recview_Results.layoutManager = LinearLayoutManager(this)

        txtvw_Latitude = findViewById(R.id.textView_GPS_latitude)
        txtvw_Longitude = findViewById(R.id.textView_GPS_longitude)

        btnReady = findViewById(R.id.btn_ready_to_measure)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        presenter = MainPresenter(this)
        presenter.refreshListResult()

        if (UtilsPermissions(this).checkSelfPermission(this)) {
            employLocationManager(SLOW_INTERVAL, LONG_DISTANCE)
        }


        /* ----------- LEGACY-init---------------------------------

        displayedTime = findViewById(R.id.textView_time)
        setCurrentTime()

        displayedResultDate = findViewById(R.id.textView_date_result)
        displayedResultTime = findViewById(R.id.textView_time_result)
        displayedResult = findViewById(R.id.textView_timer_result)

        btnStart = findViewById(R.id.btn_Start)
        btnStart?.setOnClickListener { onStartClick() }
        btnStop = findViewById(R.id.btn_Stop)
        btnStop?.setOnClickListener { onBtnStopClick() }
        chrono = findViewById(R.id.chronometer)

         ------------------------------------------------------*/

    }


    // ---- ACTIVITY LIFE CYCLE --------------
    override fun onResume(){
        super.onResume()

        //If there is a rotation when timer is running, activity will restore data
        //during onRestoreInstanceState(), and here time measuring will be continue
        if (isTimerRunning) {
            presenter.initRaceView()
            presenter.continueRace()
            employLocationManager(FAST_INTERVAL, SHORT_DISTANCE)

        } else {
            presenter.initCommonView()
            employLocationManager(SLOW_INTERVAL, LONG_DISTANCE)
        }
    }

    override fun onPause() {
        super.onPause()
        locationManager?.removeUpdates(presenter.locationListener)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("centisecs", centisecs)
        outState?.putInt("secs", secs)
        outState?.putBoolean("isTimerRunning", isTimerRunning)

    }

    override fun onRestoreInstanceState(savedState: Bundle?) {
        super.onRestoreInstanceState(savedState)

        //проверка, потому что иначе жалуется на Int? вместо Int
        if (savedState != null) {
            centisecs = savedState.getInt("centisecs")
            secs = savedState.getInt("secs")
            isTimerRunning = savedState.getBoolean("isTimerRunning")
        }

    }

    fun employLocationManager(interval : Long, distance : Float) {
        try {
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, interval, distance, presenter.locationListener)
        } catch (ex: SecurityException) {
            changeGPSStatus("SecurityException") //just for test-info, later will change this line
        }
    }

    // ---- TEXTVIEWS TEXT CHANGING -------------

    fun changeGPSStatus(status : String) {
        txtvw_GPSStatus?.setText(status)
    }

    fun changeDisplayedSpeed(speed : Int) {
        txtvw_Speed?.setText(speed.toString())
    }

    fun setCurrentDate(){
        txtvw_Date?.setText(dateFormatter.format(Date()))
    }

    fun changeDisplayedCoordinates (lat: String, long: String) {
        txtvw_Latitude?.setText(lat)
        txtvw_Longitude?.setText(long)
    }

    fun setTimerStateText() {
        txtvw_CounterMillis?.setText(decisecs.toString())
        txtvw_CounterSeconds?.setText(secs.toString())
    }

    // ---- TIMER ---------

    fun initTimer() {
        timer = Timer() //возможно это костыль, но я не понял, как грамотно остановить таймер, инициализированный ранее. cancel() убивал его, schedule({cancel()}) тоже убивал... иных методов при гуглеже не нашел
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

    // ---- BUTTONS -------

    fun onReadyToRaceClick() {
        presenter.onBtnReadyClick()
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

    fun transformBtnReadyToStop() {
        btnReady?.text = getString(R.string.btn_stop_race)
        btnReady?.setOnClickListener {onBtnStopClick()}
    }

    fun onBtnStopClick() {
        presenter.onBtnStopClick()
    }

    // ---- VIEW generally -----

    fun initCommonView() {
        initBtnReady()
        setTimerStateText()
    }

    fun initRaceView() {
        transformBtnReadyToStop()
        setTimerStateText()
    }

    fun initBtnReady() {
        btnReady?.text = getString(R.string.btn_ready_to_race)
        btnReady?.setOnClickListener {onReadyToRaceClick()}
    }

    // ---- DATA-transfer and RecyclerView -----

    fun sendResult() {
        val date = dateFormatter.format(Date())
        val time = timeFormatter.format(Date())
        presenter.saveResult(date, time, secs, centisecs)
    }

    fun refreshListResult(adapter: RecViewResultsAdapter) {
        recview_Results?.adapter = adapter
    }

}
    /* ----------- LEGACY-methods ---------------------------------

    fun setCurrentTime() {
        //there will be smt to set real-time in app. Don`t know what it will be yet.
    }

    fun onStartClick() {
        chrono?.setBase(SystemClock.elapsedRealtime())
        chrono?.start()
        presenter.StartTimer()
    }

    fun onBtnStopClick() {
        chrono?.stop()
        presenter.StopTimer()
    }


    fun showResult (result: String) {
        displayedResult?.setText(result)
        displayedResultDate?.setText(dateFormatter.format(Date()))
        displayedResultTime?.setText(timeFormatter.format(Date()))
    }
     ------------------------------------------------------------*/


