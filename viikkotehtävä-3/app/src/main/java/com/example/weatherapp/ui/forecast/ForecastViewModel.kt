package com.example.weatherapp.ui.forecast

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.LocalForecastItem
import com.example.weatherapp.model.mapIconToResource
import com.example.weatherapp.network.RetrofitInstance
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class ForecastViewModel : ViewModel() {
    var city by mutableStateOf("Tampere")
        private set

    var forecast by mutableStateOf<List<LocalForecastItem>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadForecast()
    }

    fun loadForecast(cityName: String = "Tampere") {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitInstance.weatherApi.getForecast(city = cityName)
                city = response.city?.name ?: cityName

                val rawList = response.list ?: emptyList()

                val groupedByDate = rawList.groupBy { item ->
                    item.dtTxt?.substringBefore(" ") ?: ""
                }.filterKeys { it.isNotBlank() }

                val locale = Locale("fi")
                val items = groupedByDate.map { (dateStr, dayItems) ->
                    val minTemp = dayItems.minOfOrNull { it.main?.tempMin ?: it.main?.temp ?: 0.0 } ?: 0.0
                    val maxTemp = dayItems.maxOfOrNull { it.main?.tempMax ?: it.main?.temp ?: 0.0 } ?: 0.0

                    val midDayItem = dayItems.firstOrNull { it.dtTxt?.contains("12:00:00") == true }
                        ?: dayItems[dayItems.size / 2]

                    val weatherDesc = midDayItem.weather?.firstOrNull()

                    val dayName = try {
                        val parsedDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
                        "${parsedDate.dayOfWeek.getDisplayName(TextStyle.FULL, locale).replaceFirstChar { it.uppercase() }} ${parsedDate.dayOfMonth}.${parsedDate.monthValue}."
                    } catch (e: Exception) {
                        dateStr
                    }

                    LocalForecastItem(
                        date = dayName,
                        minimumTemperature = minTemp,
                        maximumTemperature = maxTemp,
                        description = weatherDesc?.description?.replaceFirstChar { it.uppercase() } ?: "",
                        iconResource = mapIconToResource(weatherDesc?.icon)
                    )
                }

                forecast = items
            } catch (e: Exception) {
                errorMessage = e.message ?: "Virhe ennusteen hakemisessa"
            } finally {
                isLoading = false
            }
        }
    }
}
