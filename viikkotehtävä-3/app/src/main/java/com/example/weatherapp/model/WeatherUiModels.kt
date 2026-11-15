package com.example.weatherapp.model

import com.example.weatherapp.R

data class LocalCurrentWeather(
    val city: String,
    val temperature: Double,
    val feelsLike: Double,
    val description: String,
    val humidity: Int,
    val windSpeed: Double,
    val iconResource: Int
)

data class LocalForecastItem(
    val date: String,
    val minimumTemperature: Double,
    val maximumTemperature: Double,
    val description: String,
    val iconResource: Int
)

fun mapIconToResource(icon: String?): Int {
    return when (icon) {
        "01d", "01n" -> R.drawable.weather_clear
        "02d", "02n" -> R.drawable.weather_partly_cloudy
        "03d", "03n", "04d", "04n" -> R.drawable.weather_cloudy
        "09d", "09n", "10d", "10n", "11d", "11n" -> R.drawable.weather_rain
        "13d", "13n" -> R.drawable.weather_snow
        else -> R.drawable.weather_unknown
    }
}
