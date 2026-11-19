package com.example.laiterajapinnat

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp



@Composable
fun ActionsView(
    context: Context = LocalContext.current,
    onRequestPhoneCall: (String) -> Unit
) {
    var emailRecipient by remember { mutableStateOf("random@tuni.fi") }
    var emailSubject by remember { mutableStateOf("Laiterajapinnat testauksessa") }
    var emailBody by remember { mutableStateOf("Hei, tässä viesti laiterajapintojen testauksesta.") }
    val scrollState = rememberScrollState()

    var phoneNumber by remember { mutableStateOf("0401234567") }



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
                Column {
                    Text(
                        text = "Sähköpostin lähetys (ACTION_SENDTO)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Avaa laitteen sähköpostisovelluksen valmiilla tiedoilla",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedTextField(
                    value = emailRecipient,
                    onValueChange = { emailRecipient = it },
                    label = { Text("Vastaanottaja") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = emailSubject,
                    onValueChange = { emailSubject = it },
                    label = { Text("Aihe") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = emailBody,
                    onValueChange = { emailBody = it },
                    label = { Text("Viesti") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        val uriString = "mailto:${Uri.encode(emailRecipient)}" +
                                "?subject=${Uri.encode(emailSubject)}" +
                                "&body=${Uri.encode(emailBody)}"

                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse(uriString)
                        }

                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(context, "Sähköpostisovellusta ei löytynyt", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Virhe avattaessa sähköpostia", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    ActionsIcon(
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Avaa sähköpostisovellus")
                }
            }
        }

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
                Column {
                    Text(
                        text = "Puhelun soittaminen (ACTION_CALL)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Soittaa puhelun annettua numeroa käyttäen",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Puhelinnumero") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = { onRequestPhoneCall(phoneNumber) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    PhoneCallIcon(
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Soita puhelu")
                }
            }
        }
    }
}

@Composable
fun PhoneCallIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        val path = Path().apply {
            moveTo(w * 0.28f, h * 0.15f)
            lineTo(w * 0.45f, h * 0.15f)
            lineTo(w * 0.52f, h * 0.35f)
            lineTo(w * 0.40f, h * 0.45f)
            cubicTo(
                w * 0.48f, h * 0.58f,
                w * 0.58f, h * 0.68f,
                w * 0.70f, h * 0.75f
            )
            lineTo(w * 0.80f, h * 0.63f)
            lineTo(w * 0.95f, h * 0.72f)
            lineTo(w * 0.88f, h * 0.92f)
            cubicTo(
                w * 0.55f, h * 0.95f,
                w * 0.20f, h * 0.60f,
                w * 0.15f, h * 0.27f
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
    }
}
