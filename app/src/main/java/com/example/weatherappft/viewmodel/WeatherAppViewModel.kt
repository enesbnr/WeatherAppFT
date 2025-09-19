package com.example.weatherappft.viewmodel
import android.util.Log
import androidx.lifecycle.*
import com.example.weatherappft.apis.WeatherAppFTDataSource
import com.example.weatherappft.model.CapitalWeather
import kotlinx.coroutines.launch


class WeatherAppViewModel(private val repository: WeatherAppFTDataSource) : ViewModel() {

    val apiKey ="YOUR_API_KEY"
    private val _capitalWeatherList = MutableLiveData<List<CapitalWeather>>()
    val capitalWeatherList: LiveData<List<CapitalWeather>> = _capitalWeatherList

    private val _selectedCityWeather = MutableLiveData<CapitalWeather>()
    val selectedCityWeather: LiveData<CapitalWeather> = _selectedCityWeather

    fun loadCapitals(cities: List<String>, apiKey: String) {
        viewModelScope.launch {
            try {
                val list = repository.getCapitalsWeather(cities, apiKey)
                _capitalWeatherList.postValue(list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadWeatherByCoords(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
           try {
               val weather = repository.getWeatherForCoordinates(lat,lon,apiKey)
               _selectedCityWeather.postValue(weather)
           }catch (e:Exception){
               e.printStackTrace()
           }


        }
    }

    fun loadSelectedCity(city: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val weather = repository.getWeatherForCity(city, apiKey)
                _selectedCityWeather.postValue(weather)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


