package com.example.weatherapp.ui.forecast

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.model.LocalForecastItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastScreen(
    city: String,
    onBackClick: () -> Unit,
    viewModel: ForecastViewModel = viewModel()
) {

    LaunchedEffect(city) {
        viewModel.loadForecast(city)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather Forecast") },
                navigationIcon = { IconButton(onClick = onBackClick) { Text("←") } }
            )
        }
    ) { innerPadding ->
        when {
            viewModel.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            viewModel.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = viewModel.errorMessage ?: "Virhe ennusteen latauksessa",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadForecast(city) }) {
                            Text("Yritä uudelleen")
                        }
                    }
                }
            }
            else -> {
                Column(Modifier.fillMaxSize().padding(innerPadding)) {
                    Text(
                        viewModel.city,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(viewModel.forecast, key = { it.date }) { item -> ForecastCard(item) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ForecastCard(item: LocalForecastItem) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(item.iconResource), item.description, Modifier.size(56.dp))
            Column(Modifier.weight(1f).padding(horizontal = 16.dp)) {
                Text(item.date, style = MaterialTheme.typography.titleMedium)
                Text(item.description)
            }
            Text("${item.minimumTemperature.toInt()}° / ${item.maximumTemperature.toInt()}°", style = MaterialTheme.typography.titleMedium)
        }
    }
}
