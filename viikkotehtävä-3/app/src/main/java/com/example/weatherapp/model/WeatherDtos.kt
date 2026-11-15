package com.example.weatherapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherApiResponse(
    val name: String? = null,
    val main: MainDto? = null,
    val weather: List<WeatherDescriptionDto>? = null,
    val wind: WindDto? = null
)

@Serializable
data class MainDto(
    val temp: Double? = null,
    @SerialName("feels_like") val feelsLike: Double? = null,
    @SerialName("temp_min") val tempMin: Double? = null,
    @SerialName("temp_max") val tempMax: Double? = null,
    val humidity: Int? = null
)

@Serializable
data class WeatherDescriptionDto(
    val id: Int? = null,
    val main: String? = null,
    val description: String? = null,
    val icon: String? = null
)

@Serializable
data class WindDto(
    val speed: Double? = null
)

@Serializable
data class ForecastApiResponse(
    val city: CityDto? = null,
    val list: List<ForecastItemDto>? = null
)

@Serializable
data class CityDto(
    val name: String? = null
)

@Serializable
data class ForecastItemDto(
    val dt: Long? = null,
    @SerialName("dt_txt") val dtTxt: String? = null,
    val main: MainDto? = null,
    val weather: List<WeatherDescriptionDto>? = null
)
