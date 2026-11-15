package com.example.weatherapp.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitInstance {

    private const val BASE_URL =
        "https://api.openweathermap.org/"

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    val weatherApi: WeatherApiService by lazy {
        val contentType = MediaType.parse("application/json")!!
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(WeatherApiService::class.java)
    }
}
