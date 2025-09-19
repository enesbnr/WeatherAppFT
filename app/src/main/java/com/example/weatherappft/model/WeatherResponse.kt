package com.example.weatherappft.model


data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind
)
data class Main(
    val temp: Float,
    val humidity:Int,
)
data class Weather(
    val description :String,
    val main:String
)
data class Wind(
    val speed:Float
)
