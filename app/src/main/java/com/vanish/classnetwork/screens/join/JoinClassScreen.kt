
package com.vanish.classnetwork.screens.join

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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF071B2C)
private val Teal = Color(0xFF00BFA6)
private val Ice = Color(0xFFF4FBFD)
private val Grey = Color(0xFF64748B)

@Composable
fun JoinClassScreen(
    studentName: String,
    onJoin: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var classId by remember { mutableStateOf("") }
    var accessCode by remember { mutableStateOf("") }

    val isValid = classId.trim().isNotEmpty() &&
            accessCode.trim().isNotEmpty()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            TextButton(onClick = onBack) {
                Text(
                    text = "← Back",
                    color = Navy,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "CLASS CONNECTION",
                    color = Teal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Join your class",
                    color = Navy,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Hello, $studentName. Enter the connection details shared by your class administrator.",
                    color = Grey,
                    fontSize = 15.sp,
                    lineHeight = 23.sp
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Ice
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "NETWORK DETAILS",
                        color = Navy,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Class Network ID",
                        color = Navy,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )

                    OutlinedTextField(
                        value = classId,
                        onValueChange = {
                            if (it.length <= 60) {
                                classId = it
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter class network ID")
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )

                    Text(
                        text = "Access code",
                        color = Navy,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )

                    OutlinedTextField(
                        value = accessCode,
                        onValueChange = {
                            if (it.length <= 40) {
                                accessCode = it
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter class access code")
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.height(36.dp),
                            shape = CircleShape,
                            color = Color(0xFFD8F5EF)
                        ) {
                            Text(
                                text = "!",
                                modifier = Modifier.padding(
                                    horizontal = 13.dp,
                                    vertical = 5.dp
                                ),
                                color = Teal,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                        }

                        Text(
                            text = "Private class access",
                            color = Navy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Text(
                        text = "Only join a class using connection details provided by your authorized class administrator. Never share your access code publicly.",
                        color = Grey,
                        fontSize = 13.sp,
                        lineHeight = 21.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    if (isValid) {
                        onJoin(
                            classId.trim(),
                            accessCode.trim()
                        )
                    }
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Navy,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFCBD5E1),
                    disabledContentColor = Color.White
                )
            ) {
                Text(
                    text = "Connect to Class",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Connection will be verified before you enter the class room.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                color = Grey,
                fontSize = 12.sp
            )
        }
    }
}
