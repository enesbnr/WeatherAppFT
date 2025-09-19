package com.example.weatherappft.model

data class CapitalWeather(
    val name: String,
    val temp: Float,
    val main:String,
    val humidity: Int,
    val description: String,
    val wind:Float
)
