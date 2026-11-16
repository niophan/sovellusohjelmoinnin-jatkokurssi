package com.example.laiterajapinnat

import android.hardware.Sensor
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun AllSensorsView(
    sensors: List<Sensor>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    val filteredSensors = remember(sensors, searchQuery) {
        if (searchQuery.isBlank()) {
            sensors
        } else {
            sensors.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.vendor.contains(searchQuery, ignoreCase = true) ||
                        getSensorTypeName(it.type).contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            placeholder = { Text("Etsi anturia nimellä tai tyypillä...") },
            leadingIcon = {
                SearchIcon(
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        ClearIcon(
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Text(
            text = "Näytetään ${filteredSensors.size} / ${sensors.size} anturia",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredSensors) { sensor ->
                SensorItemCard(sensor = sensor)
            }
        }
    }
}

@Composable
fun SensorItemCard(sensor: Sensor) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sensor.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = getSensorTypeName(sensor.type),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Valmistaja: ${sensor.vendor}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Virta: %.2f mA".format(sensor.power),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SearchIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension * 0.3f
        val center = Offset(size.width * 0.4f, size.height * 0.4f)
        drawCircle(
            color = tint,
            center = center,
            radius = radius,
            style = Stroke(width = 2.dp.toPx())
        )
        drawLine(
            color = tint,
            start = Offset(center.x + radius * 0.7f, center.y + radius * 0.7f),
            end = Offset(size.width * 0.85f, size.height * 0.85f),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun ClearIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()
        drawLine(
            color = tint,
            start = Offset(w * 0.25f, h * 0.25f),
            end = Offset(w * 0.75f, h * 0.75f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.75f, h * 0.25f),
            end = Offset(w * 0.25f, h * 0.75f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Suppress("DEPRECATION")
fun getSensorTypeName(type: Int): String {
    return when (type) {
        Sensor.TYPE_ACCELEROMETER -> "Kiihtyvyys"
        Sensor.TYPE_MAGNETIC_FIELD -> "Magneetti"
        Sensor.TYPE_ORIENTATION -> "Asento"
        Sensor.TYPE_GYROSCOPE -> "Gyroskooppi"
        Sensor.TYPE_LIGHT -> "Valoanturi"
        Sensor.TYPE_PRESSURE -> "Ilmanpaine"
        Sensor.TYPE_PROXIMITY -> "Läheisyys"
        Sensor.TYPE_GRAVITY -> "Painovoima"
        Sensor.TYPE_LINEAR_ACCELERATION -> "Lineaarinen kiihtyvyys"
        Sensor.TYPE_ROTATION_VECTOR -> "Kierto-vektori"
        Sensor.TYPE_RELATIVE_HUMIDITY -> "Kosteus"
        Sensor.TYPE_AMBIENT_TEMPERATURE -> "Lämpötila"
        Sensor.TYPE_STEP_COUNTER -> "Askelmittari"
        Sensor.TYPE_STEP_DETECTOR -> "Askeltunnistin"
        Sensor.TYPE_HEART_RATE -> "Syke"
        else -> "Tyyppi $type"
    }
}
