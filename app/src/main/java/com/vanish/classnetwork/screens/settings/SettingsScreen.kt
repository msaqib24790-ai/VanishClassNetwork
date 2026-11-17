
package com.vanish.classnetwork.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF071B2C)
private val Teal = Color(0xFF00AFA3)
private val PageBackground = Color(0xFFF5F8FA)
private val SecondaryText = Color(0xFF718096)

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onPrivacy: () -> Unit = {},
    onAbout: () -> Unit = {}
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var messagePreviewEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var autoRemoveExpired by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .statusBarsPadding()
    ) {

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clickable { onBack() },
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF0F4F7)
            ) {
                androidx.compose.foundation.layout.Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text("‹", fontSize = 32.sp, color = Navy)
                }
            }

            Column(
                modifier = Modifier.padding(start = 14.dp)
            ) {
                Text(
                    text = "Settings",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )
                Text(
                    text = "Manage your app preferences",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // Profile card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Navy
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(52.dp),
                        shape = RoundedCornerShape(17.dp),
                        color = Color(0xFF173C50)
                    ) {
                        androidx.compose.foundation.layout.Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "V",
                                fontSize = 27.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(start = 14.dp)
                    ) {
                        Text(
                            "Vanish Class Network",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Your class communication space",
                            fontSize = 12.sp,
                            color = Color(0xFFB9D0DA)
                        )
                    }
                }
            }

            SettingsSection(title = "NOTIFICATIONS") {
                SettingsSwitchRow(
                    title = "Notifications",
                    description = "Receive class message alerts",
                    checked = notificationsEnabled,
                    onCheckedChange = {
                        notificationsEnabled = it
                    }
                )

                SettingsDivider()

                SettingsSwitchRow(
                    title = "Message previews",
                    description = "Show message text in notifications",
                    checked = messagePreviewEnabled,
                    onCheckedChange = {
                        messagePreviewEnabled = it
                    }
                )

                SettingsDivider()

                SettingsSwitchRow(
                    title = "Sound",
                    description = "Play a sound for new messages",
                    checked = soundEnabled,
                    onCheckedChange = {
                        soundEnabled = it
                    }
                )

                SettingsDivider()

                SettingsSwitchRow(
                    title = "Vibration",
                    description = "Vibrate for message alerts",
                    checked = vibrationEnabled,
                    onCheckedChange = {
                        vibrationEnabled = it
                    }
                )
            }

            SettingsSection(title = "MESSAGE PREFERENCES") {
                SettingsSwitchRow(
                    title = "Remove expired messages",
                    description = "Keep expired messages out of the chat",
                    checked = autoRemoveExpired,
                    onCheckedChange = {
                        autoRemoveExpired = it
                    }
                )

                SettingsDivider()

                SettingsInfoRow(
                    title = "Message lifetime",
                    description = "Messages are designed to expire after 30 seconds",
                    value = "30 sec"
                )
            }

            SettingsSection(title = "PRIVACY & INFORMATION") {
                SettingsNavigationRow(
                    title = "Privacy & Security",
                    description = "Learn how temporary messages are handled",
                    onClick = onPrivacy
                )

                SettingsDivider()

                SettingsNavigationRow(
                    title = "About Vanish",
                    description = "App information and version details",
                    onClick = onAbout
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFEAF7F6)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Your preferences",
                        fontWeight = FontWeight.Bold,
                        color = Navy,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        "These settings currently apply during this screen session. Permanent preference storage will be implemented separately.",
                        color = Color(0xFF42636B),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Text(
                text = "Vanish Class Network • Version 1.0",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = SecondaryText,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            modifier = Modifier.padding(start = 4.dp, bottom = 9.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SecondaryText,
            letterSpacing = 1.sp
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 1.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 15.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Navy
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                description,
                fontSize = 11.sp,
                color = SecondaryText
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingsInfoRow(
    title: String,
    description: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Navy
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                description,
                fontSize = 11.sp,
                color = SecondaryText
            )
        }

        Text(
            value,
            color = Teal,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SettingsNavigationRow(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Navy
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                description,
                fontSize = 11.sp,
                color = SecondaryText
            )
        }

        Text(
            "›",
            fontSize = 27.sp,
            color = SecondaryText
        )
    }
}

@Composable
private fun SettingsDivider() {
    androidx.compose.material3.HorizontalDivider(
        color = Color(0xFFEDF1F4),
        thickness = 1.dp
    )
}
