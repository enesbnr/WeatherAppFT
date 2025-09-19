package ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherappft.adapter.CapitalWeatherAdapter
import com.example.weatherappft.R
import com.example.weatherappft.repository.WeatherAppFTRepository
import com.example.weatherappft.viewmodel.WeatherAppViewModel
import com.example.weatherappft.viewmodel.WeatherViewModelFactory
import com.example.weatherappft.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WeatherAppViewModel
    private lateinit var adapter: CapitalWeatherAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val apiKey = "API_KEY"
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView setup
        adapter = CapitalWeatherAdapter(listOf())
        binding.capitalsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.capitalsRecyclerView.adapter = adapter

        registerLauncher()

        // Repository ve Factory
        val repository = WeatherAppFTRepository()
        val factory = WeatherViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(WeatherAppViewModel::class.java)

        // Fragment runtime ekleme ve gizleme
        if (savedInstanceState == null) {
            val mapFragment = MapFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, mapFragment)
                .hide(mapFragment)
                .commit()
        }

        // Observers
        viewModel.capitalWeatherList.observe(this) { list ->
            adapter.updateData(list)

        }

        viewModel.selectedCityWeather.observe(this) { cityWeather ->
            binding.cityNameText.text = cityWeather.name
            binding.temperatureText.text = "${cityWeather.temp.toInt()}°C"
            binding.humidityText.text = "${cityWeather.humidity}%"
            binding.windText.text = "${cityWeather.wind} km/h"

            if (cityWeather != null) {
                Log.d("WeatherCheck", "API'den gelen şehir: ${cityWeather.name}")
            }
            val mainType = cityWeather.main  // "Clear", "Clouds", vs
            val iconRes = WeatherType.getType(mainType)
            binding.weatherIcon.setImageResource(iconRes)

        }

        // Capitals listesi
        viewModel.loadCapitals(listOf("Berlin", "Londra", "Ankara", "Brüksel", "Amsterdam"), apiKey)

        // Location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(binding.root, "Konumunuz paylaşılmıyor", Snackbar.LENGTH_INDEFINITE)
                    .setAction("İzin ver") {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }.show()
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        } else {
            fetchCurrentLocation()
        }

        // Map fragment gösterme
        binding.selectCityButton.setOnClickListener {
            val mapFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
            mapFragment?.let {
                supportFragmentManager.beginTransaction()
                    .show(it)
                    .commit()
            }
        }
    }
    private fun fetchCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // İzin yoksa fallback olarak İstanbul'un koordinatlarıyla alabiliriz
            val istanbulLat = 41.0082
            val istanbulLon = 28.9784
            viewModel.loadWeatherByCoords(istanbulLat, istanbulLon, apiKey)
            return
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude
                viewModel.loadWeatherByCoords(lat, lon, apiKey)
                // Observer ile loglama
                viewModel.selectedCityWeather.observe(this) { weather ->
                    weather?.let {
                        Log.d("WeatherCheck", "City: ${it.name}, Temp: ${it.temp}, Humidity: ${it.humidity}, Wind: ${it.wind}, Desc: ${it.description}")
                    }
                }
            } else {
                // Location null ise fallback
                val istanbulLat = 41.0082
                val istanbulLon = 28.9784
                viewModel.loadWeatherByCoords(istanbulLat, istanbulLon, apiKey)
            }
        }
    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                fetchCurrentLocation()
            } else {
                Toast.makeText(this, "İzin verilmedi", Toast.LENGTH_LONG).show()
            }
        }
    }
}

enum class WeatherType(val main: String, @DrawableRes val id: Int){
    SUNNY("Clear", R.drawable.sunny_selected),
    CLOUDS("Clouds", R.drawable.cloudy),
    RAIN("Rain", R.drawable.rain_selected),
    THUNDERSTORM("Thunderstorm", R.drawable.thunder_selected),
    SNOW("Snow", R.drawable.snow_selected);

    companion object {
        fun getType(type: String): Int {
            return WeatherType.values().find { it.main == type }?.id ?: R.drawable.sunny
        }
    }
}


//"Clear"
//
//"Clouds"
//
//"Rain"
//
//"Snow"
//
//"Thunderstorm"

//enum class WeatherType(val desc : String,@DrawableRes val id : Int){
//    SUNNY("clear sky",R.drawable.sunny),
//    FEWCLOUDS("few clouds",R.drawable.cloudy),
//    SCATTERED("scattered clouds",R.drawable.cloudy),
//    RAIN("rain",R.drawable.rain),
//    THUNDERSTORM("thunderstorm",R.drawable.thunder),
//    SNOW("snow",R.drawable.snow);
//
//    companion object {
//        fun getType(type:String ) :Int {
//            return WeatherType.entries.find { it.desc == type }?.id ?: R.drawable.sunny
//        }
//    }
//}

//"clear sky" -> R.drawable.sunny
// "few clouds" -> R.drawable.cloudy
// "scattered clouds" -> R.drawable.cloudy
// "thunderstorm" -> R.drawable.thunder
// else -> R.drawable.sunny
