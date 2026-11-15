package com.example.weatherapp.ui.current

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.model.LocalCurrentWeather

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentWeatherScreen(
    onForecastClick: (String) -> Unit,
    viewModel: CurrentWeatherViewModel = viewModel()
) {
    Scaffold(topBar = { TopAppBar(title = { Text("Current Weather") }) }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val weather = viewModel.weather

            if (weather != null) {
                Text(weather.city, style = MaterialTheme.typography.headlineMedium)
            }

            when {
                viewModel.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                viewModel.errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = viewModel.errorMessage ?: "Virhe sään latauksessa",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                weather != null -> {
                    CurrentWeatherCard(weather)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Last updated: ${viewModel.currentTime ?: ""}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Button(
                onClick = { viewModel.loadWeather() },
                enabled = !viewModel.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Load weather")
            }

            Button(onClick = { viewModel.weather?.let {
                onForecastClick(it.city)
            } }, modifier = Modifier.fillMaxWidth()) {
                Text("View forecast")
            }
        }
    }
}

@Composable
fun CurrentWeatherCard(weather: LocalCurrentWeather) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painterResource(weather.iconResource), weather.description, Modifier.size(100.dp))
            Spacer(Modifier.height(8.dp))
            Text("${weather.temperature} °C", style = MaterialTheme.typography.displayMedium)
            Text(weather.description, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                WeatherValue("Feels like", "${weather.feelsLike} °C")
                WeatherValue("Humidity", "${weather.humidity} %")
                WeatherValue("Wind", "${weather.windSpeed} m/s")
            }
        }
    }
}

@Composable
private fun WeatherValue(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}
