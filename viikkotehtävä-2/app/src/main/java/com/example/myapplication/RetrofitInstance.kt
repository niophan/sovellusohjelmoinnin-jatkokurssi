package com.example.myapplication

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitInstance {

    private const val BASE_URL =
        "https://jsonplaceholder.typicode.com/"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val retrofit =
        Retrofit.Builder()  // Rakennetaan yhteys palvelimelle
            .baseUrl(BASE_URL) // Palvelimen osoite
            .addConverterFactory( // Muutetaan JSON -> Kotlin
                json.asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .build()

    val api: TodoApiService =
        retrofit.create(
            TodoApiService::class.java
        )
}
