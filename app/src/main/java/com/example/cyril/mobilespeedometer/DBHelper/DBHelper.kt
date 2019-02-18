package com.example.cyril.mobilespeedometer.DBHelper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.cyril.mobilespeedometer.MainPresenter
import com.example.cyril.mobilespeedometer.Model.Result.Result

class DBHelper (context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VER), IDBObservable {

    private var observer: MainPresenter? = null

    init {
        this.observer = observer
    }

    companion object {
        private val DATABASE_VER = 1
        private val DATABASE_NAME =  "RESULTS.db"

        //Table
        private val TABLE_NAME = "Results"
        private val COL_ID = "Id"
        private val COL_DATE = "Date"
        private val COL_TIME = "Time"
        private val COL_RESULT_TIME = "ResultTime"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY = ("CREATE TABLE $TABLE_NAME($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_DATE TEXT, $COL_TIME TEXT, $COL_RESULT_TIME TEXT)")

        db?.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)

    }

    override fun registerObserver(o: MainPresenter) {
        observer = o
    }

    override fun removeObserver(o: MainPresenter) {
        observer = null
    }

    override fun notifyObservers() {
        observer?.onDBUpdated()
    }

    //CRUD
    val allResults: List<Result>
        get() {
            val listResults = ArrayList<Result>()
            val selectQuery = "SELECT * FROM $TABLE_NAME"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst())
            {
                do {
                    val result = Result()
                    result.id = cursor.getInt(cursor.getColumnIndex(COL_ID))
                    result.date = cursor.getString(cursor.getColumnIndex(COL_DATE))
                    result.time = cursor.getString(cursor.getColumnIndex(COL_TIME))
                    result.resultTime = cursor.getString(cursor.getColumnIndex(COL_RESULT_TIME))

                    listResults.add(result)
                } while (cursor.moveToNext())
            }
            db.close()
            return listResults
        }

    fun addResult(result: Result) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_DATE, result.date)
        values.put(COL_TIME, result.time)
        values.put(COL_RESULT_TIME, result.resultTime)

        db.insert(TABLE_NAME, null, values)
        db.close()
        notifyObservers()
    }

    //fun updateResult(result: Result) {}
    //not necessary

    fun deleteResult(result: Result){
        val db = this.writableDatabase

        db.delete(TABLE_NAME, "$COL_ID = ?", arrayOf(result.id.toString()))
        db.close()
        notifyObservers()
    }

    fun getLastId(): Int {
        return allResults.last().id
    }



}