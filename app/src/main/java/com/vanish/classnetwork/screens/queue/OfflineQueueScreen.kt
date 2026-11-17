
package com.vanish.classnetwork.screens.queue

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

private val QueueNavy = Color(0xFF071B2C)
private val QueueTeal = Color(0xFF00BFA6)
private val QueueGrey = Color(0xFF64748B)
private val QueueIce = Color(0xFFF4FBFD)

data class QueuedMessage(
    val id: String,
    val text: String,
    val queuedAt: String,
    val remainingSeconds: Int,
    val status: String
)

@Composable
fun OfflineQueueScreen(
    isOnline: Boolean,
    queuedMessages: List<QueuedMessage>,
    onRetry: () -> Unit,
    onRemoveExpired: () -> Unit,
    onBack: () -> Unit
) {
    val validMessages = queuedMessages.filter {
        it.remainingSeconds > 0
    }

    val expiredCount = queuedMessages.size - validMessages.size

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
                    color = QueueNavy,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "MESSAGE MANAGEMENT",
                    color = QueueTeal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Offline message queue",
                    color = QueueNavy,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Review messages waiting for a valid network connection.",
                    color = QueueGrey,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isOnline)
                        Color(0xFFEAFBF5)
                    else
                        Color(0xFFFFF7ED)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Text(
                        text = if (isOnline)
                            "NETWORK AVAILABLE"
                        else
                            "YOU ARE OFFLINE",
                        color = QueueNavy,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = if (isOnline)
                            "A network is available. Only messages that are still valid may be retried."
                        else
                            "Queued messages cannot be delivered until network connectivity returns.",
                        color = QueueGrey,
                        fontSize = 13.sp,
                        lineHeight = 21.sp
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QueueCounter(
                    title = "Waiting",
                    count = validMessages.size.toString(),
                    modifier = Modifier.weight(1f)
                )

                QueueCounter(
                    title = "Expired",
                    count = expiredCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "QUEUED MESSAGES",
                color = QueueNavy,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            if (queuedMessages.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = QueueIce
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(26.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
                        Text(
                            text = "✓",
                            color = QueueTeal,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Queue is empty",
                            color = QueueNavy,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "There are no messages waiting for delivery.",
                            color = QueueGrey,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                queuedMessages.forEach { message ->
                    QueueMessageCard(message)
                }
            }

            Button(
                onClick = onRetry,
                enabled = isOnline && validMessages.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = QueueNavy,
                    disabledContainerColor = Color(0xFFCBD5E1)
                )
            ) {
                Text(
                    text = "Retry Valid Messages",
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onRemoveExpired,
                enabled = expiredCount > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = QueueTeal,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFCBD5E1)
                )
            ) {
                Text(
                    text = "Remove Expired Messages",
                    fontWeight = FontWeight.Bold
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = QueueIce
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "IMPORTANT DELIVERY RULE",
                        color = QueueNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    Text(
                        text = "A queued message must not be delivered after its relay-defined expiry time. Expired messages must be dropped. Retrying must never create duplicate deliveries.",
                        color = QueueGrey,
                        fontSize = 13.sp,
                        lineHeight = 21.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun QueueCounter(
    title: String,
    count: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = QueueIce
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = count,
                color = QueueNavy,
                fontSize = 27.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = title,
                color = QueueGrey,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun QueueMessageCard(
    message: QueuedMessage
) {
    val expired = message.remainingSeconds <= 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (expired)
                Color(0xFFFFF1F2)
            else
                Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(17.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expired) "EXPIRED" else "WAITING",
                    color = if (expired)
                        Color(0xFFDC2626)
                    else
                        QueueTeal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )

                Text(
                    text = if (expired)
                        "Expired"
                    else
                        "${message.remainingSeconds}s left",
                    color = QueueGrey,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = message.text,
                color = QueueNavy,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 22.sp
            )

            HorizontalDivider(color = Color(0xFFE2E8F0))

            Text(
                text = "Queued: ${message.queuedAt}",
                color = QueueGrey,
                fontSize = 12.sp
            )

            Text(
                text = "Status: ${message.status}",
                color = QueueGrey,
                fontSize = 12.sp
            )

            Text(
                text = "ID: ${message.id}",
                color = QueueGrey,
                fontSize = 11.sp
            )
        }
    }
}
