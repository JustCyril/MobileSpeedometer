package com.example.cyril.mobilespeedometer.Model.Result

class Result {
    var id: Int = 0
    var date: String? = null
    var time: String? = null
    var resultTime: String? = null //0-100 time as is

    constructor() {}

    constructor(id: Int, date: String, time: String, resultTime: String){
        this.id = id
        this.date = date
        this.time = time
        this.resultTime = resultTime
    }
}