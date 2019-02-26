package com.example.cyril.mobilespeedometer.model.repository

import android.content.Context
import com.example.cyril.mobilespeedometer.model.Result
import com.example.cyril.mobilespeedometer.utils.db.DBHelper
import io.reactivex.Observable
import java.lang.Error

class Repository(private val context: Context): RepositoryContract {

    override fun getAllResult(): Observable<List<Result>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addResult(): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteResult(): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun helloWorld(): Observable<String> {

        return Observable.just(DBHelper(context).getHelloWorld())
    }


}