
package com.vanish.classnetwork.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF071B2C)
private val Teal = Color(0xFF00BFA6)
private val Ice = Color(0xFFF4FBFD)
private val Grey = Color(0xFF64748B)
private val BorderGrey = Color(0xFFE2E8F0)

data class ChatMessage(
    val id: String,
    val sender: String,
    val text: String,
    val remainingSeconds: Int,
    val isMine: Boolean
)

@Composable
fun ChatScreen(
    studentName: String,
    className: String,
    isConnected: Boolean,
    internetAvailable: Boolean = false,
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit,
    onMessageClick: (String) -> Unit = {}
) {
    var messageText by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // Chat header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy)
                    .padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                TextButton(onClick = onBack) {
                    Text(
                        text = "â† Back",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(15.dp),
                        color = Color(0xFF173D50)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "V",
                                color = Color.White,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = className,
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Vanish Class Network",
                            color = Color(0xFFB8CDD6),
                            fontSize = 12.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isConnected) {
                            Color(0xFF164E45)
                        } else {
                            Color(0xFF713B3B)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 7.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(7.dp),
                                shape = CircleShape,
                                color = if (isConnected) {
                                    Color(0xFF4ADE80)
                                } else {
                                    Color(0xFFF87171)
                                }
                            ) {}

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = if (isConnected) "Connected" else "Offline",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Expiry information
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF0FAF8)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = Color(0xFFD8F5EF)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "30s",
                                color = Teal,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Temporary messages",
                            color = Navy,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (isConnected) {
                                "Messages expire 30 seconds after relay receipt."
                            } else {
                                "Messages are kept in a temporary local queue while offline."
                            },
                            color = Grey,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Chat title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CLASS CONVERSATION",
                    color = Navy,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${messages.size} messages",
                    color = Grey,
                    fontSize = 11.sp
                )
            }

            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = BorderGrey
            )

            // Messages area
            if (messages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(76.dp),
                            shape = CircleShape,
                            color = Ice
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "V",
                                    color = Navy,
                                    fontSize = 35.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Text(
                            text = "Your class conversation starts here",
                            color = Navy,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Send a temporary message to begin communicating with your classmates.",
                            color = Grey,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = messages,
                        key = { it.id }
                    ) { message ->

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (message.isMine) {
                                Arrangement.End
                            } else {
                                Arrangement.Start
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(0.84f)
                                    .background(
                                        color = if (message.isMine) {
                                            Color(0xFFE8F7F5)
                                        } else {
                                            Ice
                                        },
                                        shape = RoundedCornerShape(17.dp)
                                    )
                                    .clickable {
                                        onMessageClick(message.id)
                                    }
                                    .padding(13.dp)
                            ) {
                                Text(
                                    text = if (message.isMine) {
                                        "You"
                                    } else {
                                        message.sender
                                    },
                                    color = Teal,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(5.dp))

                                Text(
                                    text = message.text,
                                    color = Navy,
                                    fontSize = 14.sp,
                                    lineHeight = 21.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (message.remainingSeconds > 0) {
                                            "Expires in ${message.remainingSeconds}s"
                                        } else {
                                            "Expired"
                                        },
                                        color = Grey,
                                        fontSize = 10.sp
                                    )

                                    Spacer(modifier = Modifier.weight(1f))

                                    Text(
                                        text = "Details â†’",
                                        color = Teal,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Message composer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Divider(color = BorderGrey)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = Ice
                    ) {
                        BasicTextField(
                            value = messageText,
                            onValueChange = {
                                if (it.length <= 1000) {
                                    messageText = it
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 15.dp,
                                    vertical = 15.dp
                                ),
                            textStyle = TextStyle(
                                color = Navy,
                                fontSize = 14.sp
                            ),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Box {
                                    if (messageText.isEmpty()) {
                                        Text(
                                            text = "Write a message...",
                                            color = Grey,
                                            fontSize = 14.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(9.dp))

                    Button(
                        onClick = {
                            val trimmedMessage = messageText.trim()

                            if (trimmedMessage.isNotEmpty()) {
                                onSendMessage(trimmedMessage)
                                messageText = ""
                            }
                        },
                        enabled = messageText.isNotBlank(),
                        modifier = Modifier.height(50.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Teal,
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFCBD5E1)
                        )
                    ) {
                        Text(
                            text = if (isConnected) "Send" else "Queue",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Signed in as $studentName",
                    color = Grey,
                    fontSize = 10.sp
                )
            }
        }
    }
}

