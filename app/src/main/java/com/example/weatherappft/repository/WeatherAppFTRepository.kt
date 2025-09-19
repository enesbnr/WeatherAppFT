package com.example.weatherappft.repository

import com.example.weatherappft.apis.WeatherApi
import com.example.weatherappft.apis.WeatherAppFTDataSource
import com.example.weatherappft.model.CapitalWeather
import retrofit2.Response

class WeatherAppFTRepository: WeatherAppFTDataSource {
    private val api = WeatherApi.create()

    override suspend fun getCapitalsWeather(cities: List<String>, apiKey: String): List<CapitalWeather> {
        return cities.map { city ->
            val response = api.getWeather(city, apiKey)
            CapitalWeather(
                name = response.name,
                temp = response.main.temp,
                humidity = response.main.humidity,
                wind = response.wind.speed,
                description = response.weather[0].description,
                main = response.weather[0].main
            )
        }
    }


    override suspend fun getWeatherForCity(city:String,apiKey: String): CapitalWeather {
        val response = api.getWeather(city, apiKey)
        return CapitalWeather(
            name = response.name,
            temp = response.main.temp,
            humidity = response.main.humidity,
            wind = response.wind.speed,
            description = response.weather[0].description,
            main= response.weather[0].main
        )
    }

    override suspend fun getWeatherForCoordinates(lat: Double, lon: Double, apiKey: String): CapitalWeather {
        val response =api.getWeatherByCoordinates(lat,lon,apiKey)
        return CapitalWeather(
        name = response.name,
        temp = response.main.temp,
        humidity = response.main.humidity,
        wind = response.wind.speed,
        description = response.weather[0].description, main = response.weather[0].main

        )
            }
}

