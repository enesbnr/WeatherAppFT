package com.example.weatherappft.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherappft.R
import com.example.weatherappft.databinding.CapitalCitiesWeatherBinding
import com.example.weatherappft.model.CapitalWeather

class CapitalWeatherAdapter(private var data: List<CapitalWeather>) :
    RecyclerView.Adapter<CapitalWeatherAdapter.CapitalViewHolder>() {

    class CapitalViewHolder(val binding: CapitalCitiesWeatherBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CapitalViewHolder {
        val binding = CapitalCitiesWeatherBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CapitalViewHolder(binding)
    }
    override fun onBindViewHolder(holder: CapitalViewHolder, position: Int) {
        val item = data[position]
        holder.binding.capitalNameText.text = item.name
        holder.binding.tempText.text = "${item.temp.toInt()}°C"
        holder.binding.humidityText.text = "${item.humidity}%"
        holder.binding.windText.text = "${item.wind} km/h"
        holder.binding.weatherDescriptionText.text = item.description

        val iconRes = WeatherType.getType(item.main)
        holder.binding.capitalCityIcon.setImageResource(iconRes)

    }

    override fun getItemCount() = data.size

    fun updateData(newData: List<CapitalWeather>) {
        data = newData
        notifyDataSetChanged()
    }
}
enum class WeatherType(val key: String, @DrawableRes val id: Int) {
    SUNNY("Clear", R.drawable.sunny),
    CLOUDS("Clouds", R.drawable.cloud),
    RAIN("Rain", R.drawable.rain),
    THUNDERSTORM("Thunderstorm", R.drawable.thunder),
    SNOW("Snow", R.drawable.snow);

    companion object {
        fun getType(type: String): Int {
            return WeatherType.values().find { it.key.equals( type , ignoreCase = true) }?.id ?: R.drawable.sunny
        }
    }
}
