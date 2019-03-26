package com.example.cyril.mobilespeedometer.model.repository

import io.reactivex.Observable
import com.example.cyril.mobilespeedometer.model.Result

interface RepositoryContract {

    fun getAllResult(): Observable<List<Result>>
    fun addResult(result: Result): Observable<Boolean>
    fun deleteResult(result: Result): Observable<Boolean>
}