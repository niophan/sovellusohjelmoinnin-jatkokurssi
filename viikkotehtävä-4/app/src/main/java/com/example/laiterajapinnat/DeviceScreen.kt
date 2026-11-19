package com.example.laiterajapinnat

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceScreen() {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val lightSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) }
    val allSensors = remember { sensorManager.getSensorList(Sensor.TYPE_ALL) }

    var isListening by remember { mutableStateOf(false) }
    var lightValue by remember { mutableFloatStateOf(0f) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    var location by remember {mutableStateOf<Location?>(null)}
    var status by remember { mutableStateOf("Sijainti ei olla vielä haettu.")}
    var latitude by remember { mutableDoubleStateOf(0.0)}
    var longitude by remember { mutableDoubleStateOf(0.0)}

    fun dialPhone(
        context: Context,
        phoneNumber: String
    ) {
        val intent = Intent(
            Intent.ACTION_DIAL,
            "tel:$phoneNumber".toUri()
        )
        context.startActivity(intent)
    }

    fun makePhoneCall(
        context: Context,
        phoneNumber: String
    ) {
        val intent = Intent(
            Intent.ACTION_CALL,
            "tel:$phoneNumber".toUri()
        )

        try {
            context.startActivity(intent)
        } catch (e: SecurityException) {
            Toast.makeText(
                context,
                "Puhelulupa puuttuu",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "Puhelinsovellusta ei löytynyt",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    fun readLocation() {
        status = "Haetaan sijaintia..."
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        try {

            // Indoor block with GPS_Provider will always return null
            val loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (loc != null) {
                location = loc
                latitude = loc.latitude
                longitude = loc.longitude
                status = "Sijainti haettu!"
            } else {
                status = "Sijainti ei saatavilla"
            }
        } catch (e: SecurityException) {
            status = "Ei lupaa sijaintiin"
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        Toast.makeText(
            context,
            "Granted = $permissions",
            Toast.LENGTH_LONG
        ).show()
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        val callGranted = permissions[Manifest.permission.CALL_PHONE] ?: false
        if (fineGranted || coarseGranted) {
            readLocation()
            status = "Sijainti haettu!"
        } else {
            status = "Ei lupa alokaatioon"
        }

        if (callGranted) {
            Toast.makeText(context, "Puhelu lupaan", Toast.LENGTH_SHORT).show()
        }
    }

    // Hallitaan anturin kuuntelijan elinkaarta turvallisesti
    DisposableEffect(isListening) {
        if (isListening && lightSensor != null) {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    event?.let {
                        if (it.sensor.type == Sensor.TYPE_LIGHT) {
                            lightValue = it.values[0]
                        }
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
            sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_UI)

            onDispose {
                sensorManager.unregisterListener(listener)
            }
        } else {
            onDispose { }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Laiterajapinnat",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Laiteanturit & Mittaukset",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Valoanturi") },
                    icon = {
                        LightSensorIcon(
                            tint = if (selectedTabIndex == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Kaikki anturit (${allSensors.size})") },
                    icon = {
                        ListIcon(
                            tint = if (selectedTabIndex == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("Yleiset toiminnot") },
                    icon = {
                        ActionsIcon(
                            tint = if (selectedTabIndex == 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )


                Tab(
                    selected = selectedTabIndex == 3,
                    onClick = { selectedTabIndex = 3 },
                    text = { Text("Sijainti") },
                    icon = {
                        LocationIcon(
                            tint = if (selectedTabIndex == 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
            }

            when (selectedTabIndex) {
                0 -> LightSensorView(
                    lightSensor = lightSensor,
                    isListening = isListening,
                    onListeningChange = { isListening = it },
                    lightValue = lightValue
                )
                1 -> AllSensorsView(
                    sensors = allSensors,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it }
                )
                2 -> ActionsView(
                    context = context,
                    onRequestPhoneCall = { phoneNumber ->
                        val hasCallPermission =
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CALL_PHONE
                            ) == PackageManager.PERMISSION_GRANTED

                        val shouldShowRationale =
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                context as Activity,
                                Manifest.permission.CALL_PHONE
                            )

                        if (hasCallPermission) {
                            makePhoneCall(context, phoneNumber)
                        } else if (!shouldShowRationale) {
                            dialPhone(context, phoneNumber)
                        } else {
                            permissionLauncher.launch(arrayOf(Manifest.permission.CALL_PHONE))
                        }
                    }
                )
                3 -> LocationView(
                    status = status,
                    location = location,
                    onRequestLocation = {
                        location = null
                        status = "Haetaan sijaintia..."
                        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            readLocation()
                            status = "Sijainti haettu!"
                        } else {
                            status = "Ei lupa sijaintiin"
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                )
                            )
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun ListIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val h = size.height
        val w = size.width
        val strokeWidth = 2.dp.toPx()
        for (i in 0..2) {
            val y = h * (0.25f + i * 0.25f)
            drawCircle(color = tint, center = Offset(w * 0.15f, y), radius = strokeWidth)
            drawLine(
                color = tint,
                start = Offset(w * 0.35f, y),
                end = Offset(w * 0.85f, y),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun ActionsIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        val path = Path().apply {
            moveTo(w * 0.55f, h * 0.1f)
            lineTo(w * 0.2f, h * 0.55f)
            lineTo(w * 0.48f, h * 0.55f)
            lineTo(w * 0.42f, h * 0.9f)
            lineTo(w * 0.8f, h * 0.45f)
            lineTo(w * 0.52f, h * 0.45f)
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

@Composable
fun LocationIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.9f)
            cubicTo(
                w * 0.15f, h * 0.55f,
                w * 0.15f, h * 0.12f,
                w * 0.5f, h * 0.12f
            )
            cubicTo(
                w * 0.85f, h * 0.12f,
                w * 0.85f, h * 0.55f,
                w * 0.5f, h * 0.9f
            )
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
        drawCircle(
            color = tint,
            center = Offset(w * 0.5f, h * 0.38f),
            radius = w * 0.12f
        )
    }
}
