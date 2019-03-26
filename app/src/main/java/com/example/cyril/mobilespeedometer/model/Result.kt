package com.example.cyril.mobilespeedometer.model

class Result {
    var id: Int = 0
    var date: String? = null
    var time: String? = null
    var resultTime: String? = null //0-100 km/h time as is

    constructor() {}

    constructor(id: Int, date: String, time: String, resultTime: String){
        this.id = id
        this.date = date
        this.time = time
        this.resultTime = resultTime
    }
}