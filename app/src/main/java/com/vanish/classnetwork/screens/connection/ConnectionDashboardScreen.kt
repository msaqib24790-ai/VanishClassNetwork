
package com.vanish.classnetwork.screens.connection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DashboardNavy = Color(0xFF071B2C)
private val DashboardTeal = Color(0xFF00BFA6)
private val DashboardGrey = Color(0xFF64748B)
private val DashboardIce = Color(0xFFF4FBFD)

@Composable
fun ConnectionDashboardScreen(
    studentName: String,
    classNetworkId: String,
    connectionStatus: String,
    relayStatus: String,
    lastHeartbeat: String,
    onRefresh: () -> Unit,
    onBack: () -> Unit
) {
    val isConnected = connectionStatus.equals(
        "Connected",
        ignoreCase = true
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            TextButton(onClick = onBack) {
                Text(
                    text = "← Back",
                    color = DashboardNavy,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "NETWORK MONITOR",
                    color = DashboardTeal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Connection dashboard",
                    color = DashboardNavy,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Monitor your class network connection and relay information.",
                    color = DashboardGrey,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isConnected)
                        Color(0xFFEAFBF5)
                    else
                        Color(0xFFFFF7ED)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.height(12.dp),
                            shape = CircleShape,
                            color = if (isConnected)
                                DashboardTeal
                            else
                                Color(0xFFF59E0B)
                        ) {}

                        Text(
                            text = connectionStatus,
                            color = DashboardNavy,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Text(
                        text = if (isConnected)
                            "The connection is reported as active."
                        else
                            "The class network connection is not currently verified.",
                        color = DashboardGrey,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            DashboardSection(title = "STUDENT INFORMATION") {
                DashboardRow("Student name", studentName)
                DashboardRow("Class Network ID", classNetworkId)
            }

            DashboardSection(title = "RELAY SERVER") {
                DashboardRow("Relay status", relayStatus)
                DashboardRow("Last heartbeat", lastHeartbeat)
                DashboardRow("Protocol version", "Not connected")
            }

            DashboardSection(title = "CONNECTION DIAGNOSTICS") {
                DashboardRow("Network verification", "Not implemented")
                DashboardRow("Automatic reconnection", "Not implemented")
                DashboardRow("Heartbeat monitoring", "Not implemented")
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DashboardIce
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "CONNECTION PRIVACY",
                        color = DashboardNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "This dashboard currently displays supplied status values. Actual relay communication, heartbeat monitoring and automatic reconnection will be implemented separately.",
                        color = DashboardGrey,
                        fontSize = 13.sp,
                        lineHeight = 21.sp
                    )
                }
            }

            Button(
                onClick = onRefresh,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DashboardNavy
                )
            ) {
                Text(
                    text = "Refresh Dashboard",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DashboardSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Text(
                text = title,
                color = DashboardTeal,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            content()
        }
    }
}

@Composable
private fun DashboardRow(
    label: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = label,
            color = DashboardGrey,
            fontSize = 12.sp
        )

        Text(
            text = value.ifBlank { "Not available" },
            color = DashboardNavy,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        HorizontalDivider(
            color = Color(0xFFE8EEF2)
        )
    }
}
