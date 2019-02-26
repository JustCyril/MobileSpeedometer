package com.example.cyril.mobilespeedometer.model.repository

import io.reactivex.Observable
import com.example.cyril.mobilespeedometer.model.Result

interface RepositoryContract {

    fun getAllResult(): Observable<List<Result>>
    fun addResult(): Observable<Boolean>
    fun deleteResult(): Observable<Boolean>

    //Просто для примера
    fun helloWorld(): Observable<String>
}