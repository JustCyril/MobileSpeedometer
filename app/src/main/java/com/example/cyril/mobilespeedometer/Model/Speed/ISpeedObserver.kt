package com.example.cyril.mobilespeedometer.Model.Speed

interface ISpeedObserver {
    fun onSpeedChange(newSpeed: Int)
}