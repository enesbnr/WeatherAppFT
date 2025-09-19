package com.example.weatherappft.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherappft.repository.WeatherAppFTRepository

class WeatherViewModelFactory(
    private val repository: WeatherAppFTRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherAppViewModel::class.java)) {
            return WeatherAppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}