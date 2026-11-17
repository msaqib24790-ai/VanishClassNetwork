package com.vanish.classnetwork.screens.registration


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
fun StudentRegistrationScreen(
    onContinue: (String) -> Unit,
    onBack: () -> Unit
) {
    var displayName by remember {
        mutableStateOf("")
    }

    val isValid = displayName.trim().length in 2..40

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
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
                    text = "CREATE YOUR IDENTITY",
                    color = Teal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Welcome to Vanish",
                    color = Navy,
                    fontSize = 29.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Choose how your classmates will recognize you in the class network.",
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
                        text = "STUDENT PROFILE",
                        color = Navy,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Display name",
                        color = Navy,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = displayName,
                        onValueChange = {
                            if (it.length <= 40) {
                                displayName = it
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter your name")
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        supportingText = {
                            Text("${displayName.length}/40 characters")
                        }
                    )

                    Text(
                        text = "This name will appear beside your messages.",
                        color = Grey,
                        fontSize = 12.sp
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
                    Text(
                        text = "YOUR PRIVACY MATTERS",
                        color = Navy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "Vanish is designed for temporary class communication. Messages are intended to expire 30 seconds after the relay receives them.",
                        color = Grey,
                        fontSize = 13.sp,
                        lineHeight = 21.sp
                    )

                    Text(
                        text = "Your display name identifies you to classmates. Do not enter sensitive personal information.",
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
                        onContinue(displayName.trim())
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
                    text = "Continue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "You can review your identity and privacy settings later.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                color = Grey,
                fontSize = 12.sp
            )
        }
    }
}
