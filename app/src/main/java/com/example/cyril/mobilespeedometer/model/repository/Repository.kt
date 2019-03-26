package com.example.cyril.mobilespeedometer.model.repository

import android.content.Context
import com.example.cyril.mobilespeedometer.model.Result
import com.example.cyril.mobilespeedometer.utils.db.DBHelper
import io.reactivex.Observable

class Repository(private val context: Context): RepositoryContract {

    override fun getAllResult(): Observable<List<Result>> {
        return Observable.just(DBHelper(context).getAll())
    }

    override fun addResult(result: Result): Observable<Boolean> {
        return Observable.just(DBHelper(context).addResult(result))
    }

    override fun deleteResult(result: Result): Observable<Boolean> {
        return Observable.just(DBHelper(context).deleteResult(result))
    }

    fun helloWorld(): Observable<String> {
    return Observable.just(DBHelper(context).getHelloWorld())
    }


}