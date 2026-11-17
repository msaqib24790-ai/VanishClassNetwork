package com.vanish.classnetwork.screens.protocol

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ProtocolNavy = Color(0xFF071B2C)
private val ProtocolTeal = Color(0xFF00BFA6)
private val ProtocolBackground = Color(0xFFF5F8FA)
private val ProtocolMuted = Color(0xFF718096)

@Composable
fun ProtocolInfoScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ProtocolBackground)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("‹ Back", color = ProtocolNavy, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "PROTOCOL GUIDE",
                color = ProtocolMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.3.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 22.dp, bottom = 32.dp)
        ) {
            Text(
                text = "Vanish Protocol",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ProtocolNavy
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = "The communication rules governing temporary class messages.",
                color = ProtocolMuted,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = ProtocolNavy
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "PROTOCOL VERSION",
                        color = Color(0xFF9CC6D4),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    Text(
                        text = "Version 1.0",
                        color = Color.White,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(9.dp))

                    Text(
                        text = "All participating devices must follow the same agreed protocol version.",
                        color = Color(0xFFD5E3EC),
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProtocolSectionTitle("01  MESSAGE STRUCTURE")

            Text(
                text = "Every message exchanged through the relay follows a defined data structure.",
                color = ProtocolMuted,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProtocolFieldCard(
                field = "id",
                type = "Unique message identifier",
                description = "Identifies one message. Duplicate message IDs must be ignored."
            )

            ProtocolFieldCard(
                field = "sender",
                type = "Sender identity",
                description = "Contains the agreed display identity of the student sending the message."
            )

            ProtocolFieldCard(
                field = "text",
                type = "Message content",
                description = "Contains the actual text sent to the shared class network."
            )

            ProtocolFieldCard(
                field = "sent_at",
                type = "Relay send timestamp",
                description = "Records the time assigned by the relay when the message is accepted."
            )

            ProtocolFieldCard(
                field = "expires_at",
                type = "Expiry timestamp",
                description = "Defines when the message expires. The relay determines this time."
            )

            Spacer(modifier = Modifier.height(22.dp))

            ProtocolSectionTitle("02  MESSAGE LIFECYCLE")

            Text(
                text = "A message moves through defined states during its lifetime.",
                color = ProtocolMuted,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProtocolStepCard(
                number = "01",
                title = "Created",
                description = "The sender writes and creates a new message."
            )

            ProtocolStepCard(
                number = "02",
                title = "Waiting",
                description = "The message waits for an available network connection."
            )

            ProtocolStepCard(
                number = "03",
                title = "Sent",
                description = "The relay accepts the message and assigns its official timestamps."
            )

            ProtocolStepCard(
                number = "04",
                title = "Delivered",
                description = "The relay confirms delivery to the receiving participant."
            )

            ProtocolStepCard(
                number = "05",
                title = "Displayed",
                description = "The receiving application displays the message."
            )

            ProtocolStepCard(
                number = "06",
                title = "Expired or Dropped",
                description = "Expired messages are removed or discarded and must not be delivered."
            )

            Spacer(modifier = Modifier.height(22.dp))

            ProtocolSectionTitle("03  THIRTY-SECOND EXPIRY RULE")

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFEAF7F5)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "30 seconds",
                        color = ProtocolNavy,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "The relay calculates the expiry timestamp from its accepted send time. All devices must use expires_at to calculate the remaining lifetime.",
                        color = ProtocolNavy,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Important: A receiving device must not restart the countdown when a message arrives.",
                        color = Color(0xFF167A70),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 19.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            ProtocolSectionTitle("04  OFFLINE & RECONNECTION")

            ProtocolInfoCard(
                title = "Temporary offline queue",
                description = "Messages may wait locally while offline, but only while they remain valid."
            )

            ProtocolInfoCard(
                title = "Expired messages are dropped",
                description = "If a queued message expires before connectivity returns, it must be discarded instead of transmitted."
            )

            ProtocolInfoCard(
                title = "No duplicate delivery",
                description = "The same message ID must never be processed as a new message more than once."
            )

            ProtocolInfoCard(
                title = "Heartbeat and reconnection",
                description = "Participants use heartbeat signals to monitor the relay connection and follow agreed reconnection rules."
            )

            Spacer(modifier = Modifier.height(22.dp))

            ProtocolSectionTitle("05  RELAY RESPONSIBILITIES")

            ProtocolInfoCard(
                title = "Temporary message storage",
                description = "The relay holds message content in memory only and deletes it 30 seconds after its relay-defined send time."
            )

            ProtocolInfoCard(
                title = "Delivery and expiry announcements",
                description = "The relay provides delivery confirmations and expiry announcements according to the protocol."
            )

            ProtocolInfoCard(
                title = "No content logging",
                description = "Message content must not be written to disk or included in relay logs."
            )

            Spacer(modifier = Modifier.height(22.dp))

            ProtocolSectionTitle("06  CLASS-WIDE AGREEMENT")

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "One shared protocol",
                        color = ProtocolNavy,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    Text(
                        text = "Protocol changes must be discussed and agreed upon by the whole class. Individual participants must not introduce unilateral protocol changes.",
                        color = ProtocolMuted,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFFF3DF)
            ) {
                Text(
                    text = "IMPLEMENTATION STATUS\n\nThis screen documents the assignment protocol. Actual relay communication, heartbeat handling, server-side expiry, delivery confirmations and memory-only storage still require implementation and testing.",
                    modifier = Modifier.padding(17.dp),
                    color = Color(0xFF79551C),
                    fontSize = 12.sp,
                    lineHeight = 19.sp
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "VANISH CLASS NETWORK  •  PROTOCOL 1.0",
                modifier = Modifier.fillMaxWidth(),
                color = ProtocolMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ProtocolSectionTitle(title: String) {
    Text(
        text = title,
        color = ProtocolNavy,
        fontSize = 13.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.6.sp
    )

    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun ProtocolFieldCard(
    field: String,
    type: String,
    description: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 9.dp),
        shape = RoundedCornerShape(15.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Text(
                text = field,
                color = ProtocolTeal,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = type,
                color = ProtocolNavy,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = description,
                color = ProtocolMuted,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun ProtocolStepCard(
    number: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(11.dp),
            color = Color(0xFFE0F4F1)
        ) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number,
                    color = ProtocolTeal,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = ProtocolNavy,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = description,
                color = ProtocolMuted,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun ProtocolInfoCard(
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 9.dp),
        shape = RoundedCornerShape(15.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = ProtocolNavy,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = description,
                color = ProtocolMuted,
                fontSize = 12.sp,
                lineHeight = 19.sp
            )
        }
    }
}
