package com.example.cyril.mobilespeedometer.utils.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.cyril.mobilespeedometer.Config.Companion.COL_DATE
import com.example.cyril.mobilespeedometer.Config.Companion.COL_ID
import com.example.cyril.mobilespeedometer.Config.Companion.COL_RESULT_TIME
import com.example.cyril.mobilespeedometer.Config.Companion.COL_TIME
import com.example.cyril.mobilespeedometer.Config.Companion.DATABASE_NAME
import com.example.cyril.mobilespeedometer.Config.Companion.DATABASE_VER
import com.example.cyril.mobilespeedometer.Config.Companion.HELLO
import com.example.cyril.mobilespeedometer.Config.Companion.TABLE_NAME
import com.example.cyril.mobilespeedometer.main.MainPresenter
import com.example.cyril.mobilespeedometer.model.Result

class DBHelper (context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VER), IDBObservable {

    private var observer: MainPresenter? = null

    init {
        this.observer = observer
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

    fun getAll():List<Result> {
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

/*    fun getLastId(): Int {
        return allResults.last().id
    }*/


    fun getHelloWorld(): String {
        return HELLO
    }



}