package com.example.laiterajapinnat

import android.hardware.Sensor
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LightSensorView(
    lightSensor: Sensor?,
    isListening: Boolean,
    onListeningChange: (Boolean) -> Unit,
    lightValue: Float
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Anturin tilakortti
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (lightSensor != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (lightSensor != null) {
                                    LightSensorIcon(
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(26.dp)
                                    )
                                } else {
                                    WarningIcon(
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                        }
                        Column {
                            Text(
                                text = "Valoanturi (TYPE_LIGHT)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = lightSensor?.name ?: "Anturia ei löytynyt",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = isListening,
                        onCheckedChange = onListeningChange,
                        enabled = lightSensor != null
                    )
                }

                HorizontalDivider()

                // Mittaustuloksen näyttö
                if (lightSensor != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = if (isListening) "Nykyinen valoisuus" else "Anturi pysäytetty",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isListening) "%.1f lx".format(lightValue) else "-- lx",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isListening) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (isListening) {
                            AssistChip(
                                onClick = { },
                                label = { Text(getLightDescription(lightValue)) },
                                leadingIcon = {
                                    CheckCircleIcon(
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Visuaalinen valoisuusindikaattori
                            val progress = (lightValue / 1000f).coerceIn(0f, 1f)
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "0 lx (Pimeä)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "1000+ lx (Kirkas)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp)),
                                )
                            }
                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WarningIcon(
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Tämä laite ei tue valoanturia.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // Lisätiedot anturista
        if (lightSensor != null) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Anturin tekniset tiedot",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    SensorDetailRow(label = "Valmistaja", value = lightSensor.vendor)
                    SensorDetailRow(label = "Enimmäisalue", value = "%.1f lx".format(lightSensor.maximumRange))
                    SensorDetailRow(label = "Erottelutarkkuus", value = "%.4f lx".format(lightSensor.resolution))
                    SensorDetailRow(label = "Virrankulutus", value = "%.3f mA".format(lightSensor.power))
                }
            }
        }
    }
}

@Composable
fun SensorDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun LightSensorIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension * 0.25f
        drawCircle(color = tint, center = center, radius = radius)

        val rayCount = 8
        val rayStart = radius * 1.5f
        val rayLength = radius * 0.8f
        for (i in 0 until rayCount) {
            val angle = i * (Math.PI * 2 / rayCount)
            val start = Offset(
                center.x + rayStart * cos(angle).toFloat(),
                center.y + rayStart * sin(angle).toFloat()
            )
            val end = Offset(
                center.x + (rayStart + rayLength) * cos(angle).toFloat(),
                center.y + (rayStart + rayLength) * sin(angle).toFloat()
            )
            drawLine(color = tint, start = start, end = end, strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
        }
    }
}

@Composable
fun CheckCircleIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension * 0.45f
        drawCircle(color = tint, center = center, radius = radius, style = Stroke(width = 1.5.dp.toPx()))

        val path = Path().apply {
            moveTo(size.width * 0.3f, size.height * 0.5f)
            lineTo(size.width * 0.45f, size.height * 0.65f)
            lineTo(size.width * 0.7f, size.height * 0.35f)
        }
        drawPath(path = path, color = tint, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun WarningIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.15f)
            lineTo(w * 0.9f, h * 0.85f)
            lineTo(w * 0.1f, h * 0.85f)
            close()
        }
        drawPath(path = path, color = tint, style = Stroke(width = 2.dp.toPx()))
        drawLine(
            color = tint,
            start = Offset(w * 0.5f, h * 0.4f),
            end = Offset(w * 0.5f, h * 0.6f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawCircle(color = tint, center = Offset(w * 0.5f, h * 0.72f), radius = 1.5.dp.toPx())
    }
}

private fun getLightDescription(lux: Float): String {
    return when {
        lux < 5f -> "Täysin pimeä"
        lux < 20f -> "Hämärä"
        lux < 100f -> "Himmeä sisävalo"
        lux < 400f -> "Normaali sisävalaistus"
        lux < 1000f -> "Kirkas sisävalaistus"
        lux < 10000f -> "Pilvinen ulkoilma"
        else -> "Kirkas auringonpaiste"
    }
}
