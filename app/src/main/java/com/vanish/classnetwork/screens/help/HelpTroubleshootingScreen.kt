package com.vanish.classnetwork.screens.help

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

private data class HelpItem(
    val question: String,
    val answer: String
)

@Composable
fun HelpTroubleshootingScreen(
    onBack: () -> Unit,
    onProtocol: () -> Unit = {}
) {
    BackHandler(onBack = onBack)

    val navy = Color(0xFF071B2C)
    val teal = Color(0xFF00AFA3)
    val muted = Color(0xFF64748B)

    var expandedItem by remember { mutableStateOf(-1) }

    val helpItems = listOf(
        HelpItem(
            "How do I join my class?",
            "Open Join Your Class and enter the Class Network ID and Access Code provided by your class. This version of the app demonstrates the joining flow; actual server-side credential verification still needs to be connected."
        ),
        HelpItem(
            "Why can't I send a message?",
            "Check whether the connection indicator shows that you are connected. In this project version, the chat is a preview and does not yet communicate with a real class relay."
        ),
        HelpItem(
            "Why do messages disappear?",
            "Vanish is designed around a 30-second message lifetime. The intended protocol uses the relay's expires_at timestamp. Actual relay-controlled deletion must be implemented and tested before it can be relied upon."
        ),
        HelpItem(
            "What happens when I go offline?",
            "The planned offline queue keeps track of messages that may still be valid. When connectivity returns, expired messages should be dropped and valid messages sent only once. Real queue storage and delivery are not yet connected."
        ),
        HelpItem(
            "Can I recover an expired message?",
            "No. The intended design does not provide message recovery after expiry. Avoid relying on temporary messages as permanent records."
        ),
        HelpItem(
            "Are my messages stored permanently?",
            "The assignment design requires the relay to keep message content in memory only and delete it after its lifetime. This behavior depends on the relay implementation and has not yet been verified in this app."
        ),
        HelpItem(
            "What should I do if the app stops responding?",
            "Try returning to the previous screen. If the problem continues, close and reopen the app. If it still happens, note the screen and the steps that caused the problem so it can be investigated."
        ),
        HelpItem(
            "Does Vanish guarantee complete privacy?",
            "No messaging app can prevent someone from taking screenshots, photographing a screen, or copying information while it is visible. Only share information with people you trust."
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F8FB))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text(
                    text = "‹  Back",
                    color = navy,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "SUPPORT CENTER",
                color = teal,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.3.sp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "How can we help?",
            fontSize = 29.sp,
            fontWeight = FontWeight.Bold,
            color = navy
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Find answers and learn how to use Vanish Class Network.",
            fontSize = 14.sp,
            color = muted,
            lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(22.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = navy)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "VANISH HELP GUIDE",
                    color = Color(0xFF65E5D8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Simple answers. Clear guidance.",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = "Browse the questions below to understand the app's features and current limitations.",
                    color = Color(0xFFD5E2EA),
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "COMMON QUESTIONS",
            color = navy,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        helpItems.forEachIndexed { index, item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .clickable {
                        expandedItem =
                            if (expandedItem == index) -1 else index
                    },
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.question,
                            modifier = Modifier.weight(1f),
                            color = navy,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 20.sp
                        )

                        Text(
                            text = if (expandedItem == index) "−" else "+",
                            color = teal,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (expandedItem == index) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color(0xFFE8EEF2))
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = item.answer,
                            color = muted,
                            fontSize = 13.sp,
                            lineHeight = 21.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE8F8F6)
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "UNDERSTAND THE PROTOCOL",
                    color = Color(0xFF087F75),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = "Learn about message states, expiry rules, offline handling and relay requirements.",
                    color = navy,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )

                TextButton(
                    onClick = onProtocol,
                    modifier = Modifier.padding(start = 0.dp)
                ) {
                    Text(
                        text = "View Protocol Information  →",
                        color = Color(0xFF087F75),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Divider(color = Color(0xFFE1E8ED))

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "VANISH CLASS NETWORK",
            modifier = Modifier.fillMaxWidth(),
            color = navy,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Help & Troubleshooting  •  Version 1.0",
            modifier = Modifier.fillMaxWidth(),
            color = muted,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}
