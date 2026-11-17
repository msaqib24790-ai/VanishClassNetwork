
package com.vanish.classnetwork.screens.chat

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

private val DetailsNavy = Color(0xFF071B2C)
private val DetailsTeal = Color(0xFF00BFA6)
private val DetailsGrey = Color(0xFF64748B)
private val DetailsIce = Color(0xFFF4FBFD)

@Composable
fun MessageDetailsScreen(
    messageId: String,
    sender: String,
    messageText: String,
    sentAt: String,
    expiresAt: String,
    remainingSeconds: Int,
    deliveryStatus: String,
    onBack: () -> Unit
) {
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
                    text = "← Back to Chat",
                    color = DetailsNavy,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "MESSAGE INFORMATION",
                    color = DetailsTeal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Message details",
                    color = DetailsNavy,
                    fontSize = 29.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Review the message information and its temporary lifecycle.",
                    color = DetailsGrey,
                    fontSize = 14.sp
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DetailsIce
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "MESSAGE CONTENT",
                        color = DetailsTeal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = messageText,
                        color = DetailsNavy,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 25.sp
                    )

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    Text(
                        text = "SENT BY",
                        color = DetailsGrey,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = sender,
                        color = DetailsNavy,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "MESSAGE LIFECYCLE",
                        color = DetailsNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Remaining lifetime",
                            color = DetailsGrey,
                            fontSize = 13.sp
                        )

                        Text(
                            text = if (remainingSeconds > 0)
                                "$remainingSeconds seconds"
                            else
                                "Expired",
                            color = if (remainingSeconds > 0)
                                DetailsTeal else Color(0xFFDC2626),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    DetailRow("Sent at", sentAt)
                    DetailRow("Expires at", expiresAt)
                    DetailRow("Delivery status", deliveryStatus)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF0FAF8)
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
                        Surface(
                            modifier = Modifier.height(30.dp),
                            shape = CircleShape,
                            color = Color(0xFFD8F5EF)
                        ) {
                            Text(
                                text = "✓",
                                modifier = Modifier.padding(
                                    horizontal = 9.dp,
                                    vertical = 4.dp
                                ),
                                color = DetailsTeal,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Temporary message privacy",
                            color = DetailsNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Text(
                        text = "Vanish messages are designed to expire 30 seconds after the relay accepts them. The actual relay-based expiry and deletion mechanism will be implemented separately.",
                        color = DetailsGrey,
                        fontSize = 13.sp,
                        lineHeight = 21.sp
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "MESSAGE ID",
                    color = DetailsGrey,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )

                Text(
                    text = messageId,
                    color = DetailsNavy,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            color = DetailsGrey,
            fontSize = 13.sp
        )

        Text(
            text = value,
            color = DetailsNavy,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
