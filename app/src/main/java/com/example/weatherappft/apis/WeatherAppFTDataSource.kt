package com.example.weatherappft.apis

import com.example.weatherappft.model.CapitalWeather

interface WeatherAppFTDataSource {
        suspend fun getCapitalsWeather(cities: List<String>, apiKey: String): List<CapitalWeather>
        suspend fun getWeatherForCity(city: String, apiKey: String): CapitalWeather
        suspend fun getWeatherForCoordinates(lat: Double, lon: Double, apiKey: String): CapitalWeather
}
