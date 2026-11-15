package com.example.weatherapp.ui.current

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.LocalCurrentWeather
import com.example.weatherapp.model.mapIconToResource
import com.example.weatherapp.network.RetrofitInstance
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class CurrentWeatherViewModel : ViewModel() {
    var weather by mutableStateOf<LocalCurrentWeather?>(null)
        private set

    var currentTime by mutableStateOf<String?>(null)

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadWeather()
    }

    fun loadWeather(city: String = "London") {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            currentTime = java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                .toString()
            try {
                val response = RetrofitInstance.weatherApi.getCurrentWeather(city = city)
                val main = response.main
                val weatherDesc = response.weather?.firstOrNull()

                weather = LocalCurrentWeather(
                    city = response.name ?: city,
                    temperature = main?.temp ?: 0.0,
                    feelsLike = main?.feelsLike ?: 0.0,
                    description = weatherDesc?.description?.replaceFirstChar { it.uppercase() } ?: "",
                    humidity = main?.humidity ?: 0,
                    windSpeed = response.wind?.speed ?: 0.0,
                    iconResource = mapIconToResource(weatherDesc?.icon)
                )
            } catch (e: Exception) {
                errorMessage = e.message ?: "Virhe sään hakemisessa"
            } finally {
                isLoading = false
            }
        }
    }
}
