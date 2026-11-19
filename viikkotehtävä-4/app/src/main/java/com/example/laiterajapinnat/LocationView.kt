package com.example.laiterajapinnat

import android.location.Location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter

@Composable
fun LocationView(
    status: String,
    location: Location?,
    onRequestLocation: () -> Unit,
) {
    val scrollState = rememberScrollState()
    var currentTime by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LocationCard(
                    status = status,
                    location = location,
                    currentTime = java.time.LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                        .toString()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onRequestLocation,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    LocationIcon(
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hae sijainti")
                }
            }
        }
    }
}

@Composable
fun LocationCard(
    status: String,
    location: Location?,
    currentTime: String
) {
    Text(
        text = "Sijaintitiedot",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    if (location != null) {
        Text(
            text = "Leveysaste (Latitude): ${location.latitude}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Pituusaste (Longitude): ${location.longitude}",
            style = MaterialTheme.typography.bodyMedium
        )
        if (location.provider != null) {
            Text(
                text = "Tarjoaja: ${location.provider}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Last updated: ${currentTime ?: ""}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

    }

    Text(
        text = "Tila: $status",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.primary
    )
}
