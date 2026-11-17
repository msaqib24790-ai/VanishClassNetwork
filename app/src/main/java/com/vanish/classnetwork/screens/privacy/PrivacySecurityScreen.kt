package com.vanish.classnetwork.screens.privacy


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign

private val PrivacyNavy = Color(0xFF071B2C)
private val PrivacyTeal = Color(0xFF00BFAF)
private val PrivacyBackground = Color(0xFFF5F8FA)
private val PrivacyText = Color(0xFF243746)
private val PrivacyMuted = Color(0xFF718096)

@Composable
fun PrivacySecurityScreen(
    onBack: () -> Unit,
    onProtocol: () -> Unit = {}
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var previewsEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrivacyBackground)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("‹ Back", color = PrivacyNavy, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "PRIVACY CENTER",
                color = PrivacyMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 22.dp, bottom = 30.dp)
        ) {
            Text(
                text = "Privacy & Security",
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                color = PrivacyNavy
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = "Understand how Vanish handles your class communication.",
                fontSize = 14.sp,
                color = PrivacyMuted,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = PrivacyNavy
            ) {
                Column(
                    modifier = Modifier.padding(22.dp)
                ) {
                    Text(
                        text = "◈",
                        fontSize = 30.sp,
                        color = PrivacyTeal
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Your privacy matters",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Vanish is designed around temporary class messages and minimal personal information.",
                        fontSize = 13.sp,
                        color = Color(0xFFD5E3EC),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = Color(0xFF183D4B)
                    ) {
                        Text(
                            text = "PREVIEW SECURITY STATUS",
                            modifier = Modifier.padding(
                                horizontal = 13.dp,
                                vertical = 8.dp
                            ),
                            color = Color(0xFF8DE9DE),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeading("DATA & MESSAGE PRIVACY")

            PrivacyInfoCard(
                icon = "◉",
                title = "Temporary messages",
                description = "The assignment design requires messages to expire 30 seconds after the relay-defined send time. Real relay-based deletion is not implemented yet."
            )

            PrivacyInfoCard(
                icon = "◎",
                title = "Student identity",
                description = "Your display name identifies you in the class interface. Real identity verification is not currently implemented."
            )

            PrivacyInfoCard(
                icon = "◇",
                title = "Class network information",
                description = "The class network ID and access code are used in the join-screen preview. Actual server-side verification is not connected yet."
            )

            Spacer(modifier = Modifier.height(22.dp))

            SectionHeading("PRIVACY CONTROLS")

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    PrivacySwitchRow(
                        title = "Notifications",
                        description = "Allow class message notifications",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = Color(0xFFE9EEF2)
                    )

                    PrivacySwitchRow(
                        title = "Message previews",
                        description = "Show message content in notifications",
                        checked = previewsEnabled,
                        onCheckedChange = { previewsEnabled = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            SectionHeading("SECURITY INFORMATION")

            PrivacyInfoCard(
                icon = "⌁",
                title = "Connection security",
                description = "A secure relay connection and authenticated message transport must be implemented and tested before the app can claim network-level protection."
            )

            PrivacyInfoCard(
                icon = "⌛",
                title = "Message expiration",
                description = "The final implementation will use the relay's expires_at timestamp rather than the time a message arrives on your device."
            )

            PrivacyInfoCard(
                icon = "▤",
                title = "Local storage",
                description = "This screen does not verify or guarantee deletion of message data from device storage. Storage and cleanup rules will be implemented separately."
            )

            Spacer(modifier = Modifier.height(22.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFEAF7F5)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Protocol transparency",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrivacyNavy
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Learn how message delivery, expiration, duplicate handling and offline behavior are designed.",
                        fontSize = 13.sp,
                        color = PrivacyText,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = onProtocol,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "View protocol information  →",
                            color = PrivacyNavy,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "VANISH CLASS NETWORK  •  PRIVACY CENTER",
                modifier = Modifier.fillMaxWidth(),
                color = PrivacyMuted,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                letterSpacing = 0.8.sp
            )
        }
    }
}

@Composable
private fun SectionHeading(title: String) {
    Text(
        text = title,
        color = PrivacyMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp
    )

    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun PrivacyInfoCard(
    icon: String,
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(17.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEAF7F5)
            ) {
                Box(
                    modifier = Modifier.size(42.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        color = PrivacyTeal,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(13.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = PrivacyNavy,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = description,
                    color = PrivacyMuted,
                    fontSize = 12.sp,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun PrivacySwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = PrivacyNavy,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                color = PrivacyMuted,
                fontSize = 12.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrivacyTeal
            )
        )
    }
}
