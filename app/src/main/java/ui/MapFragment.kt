package ui

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.weatherappft.repository.WeatherAppFTRepository
import com.example.weatherappft.viewmodel.WeatherAppViewModel
import com.example.weatherappft.viewmodel.WeatherViewModelFactory
import com.example.weatherappft.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap
    private lateinit var viewModel: WeatherAppViewModel
    private val apiKey = "API_KEY"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = WeatherAppFTRepository()
        val factory = WeatherViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), factory)[WeatherAppViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        // MapView lifecycle
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Örnek başlangıç koordinatı
        val defaultLocation = LatLng(41.0082, 28.9784) // Istanbul
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))

        // Map tıklama listener
        map.setOnMapClickListener { latLng ->

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    val cityName = addresses?.firstOrNull()?.locality
                        ?: addresses?.firstOrNull()?.subAdminArea
                        ?: addresses?.firstOrNull()?.adminArea


                    Log.d("LocationCheck", "City: $cityName")

                    if (!cityName.isNullOrEmpty()) {
                        // **ViewModel update main thread’de**
                        withContext(Dispatchers.Main) {
                            viewModel.loadSelectedCity(cityName, viewModel.apiKey)
                            // Fragment’i kapat
                            parentFragmentManager.beginTransaction()
                                .hide(this@MapFragment)
                                .commit()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("LocationCheck", "Geocoder error", e)

                    e.printStackTrace()
                }
            }


        }
    }

    // MapView lifecycle forwarding
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
        _binding = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}