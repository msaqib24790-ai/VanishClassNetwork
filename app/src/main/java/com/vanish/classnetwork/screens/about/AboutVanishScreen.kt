package com.vanish.classnetwork.screens.about

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.statusBarsPadding
import com.vanish.classnetwork.R

private val AboutNavy = Color(0xFF071B2C)
private val AboutTeal = Color(0xFF00BFA6)
private val AboutBackground = Color(0xFFF4F8FB)
private val AboutGrey = Color(0xFF64748B)

@Composable
fun AboutVanishScreen(
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AboutBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            Text(
                text = "‹  Back to Settings",
                color = AboutNavy,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .background(Color.Transparent)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AboutNavy
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.vanish_logo),
                        contentDescription = "Vanish Class Network logo",
                        modifier = Modifier.size(92.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "VANISH",
                        color = Color.White,
                        fontSize = 29.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 3.sp
                    )

                    Text(
                        text = "CLASS NETWORK",
                        color = Color(0xFF8DEBE0),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Connect with your class. Share what matters.",
                        color = Color(0xFFE2E8F0),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "VERSION 1.0",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(
                                AboutTeal,
                                RoundedCornerShape(50.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 7.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            SectionTitle("About the application")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Text(
                    text = "Vanish Class Network is a student-focused class communication application designed to provide a shared space for classmates to exchange temporary messages. Its central concept is short-lived communication, with messages intended to expire after 30 seconds according to the class relay protocol.",
                    color = AboutGrey,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            SectionTitle("Core concepts")

            InfoCard(
                title = "Temporary messages",
                description = "Messages are designed around a 30-second lifetime controlled by the relay's expiry timestamp."
            )

            InfoCard(
                title = "Shared class communication",
                description = "A common class network allows participating students to communicate in one shared class space."
            )

            InfoCard(
                title = "Offline message handling",
                description = "The protocol defines how queued messages should be checked for expiry before delivery."
            )

            InfoCard(
                title = "Privacy-focused design",
                description = "The project protocol specifies memory-only relay handling without message-content logging or disk storage."
            )

            Spacer(modifier = Modifier.height(22.dp))

            SectionTitle("Technology")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    TechnologyRow("Programming language", "Kotlin")
                    HorizontalDivider(
                        color = Color(0xFFE8EDF2),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    TechnologyRow("UI framework", "Jetpack Compose")
                    HorizontalDivider(
                        color = Color(0xFFE8EDF2),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    TechnologyRow("Design system", "Material 3")
                    HorizontalDivider(
                        color = Color(0xFFE8EDF2),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    TechnologyRow("Development tool", "Android Studio")
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            SectionTitle("Project information")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "Mobile Application Development",
                        color = AboutNavy,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    Text(
                        text = "Academic project",
                        color = AboutGrey,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Vanish Class Network demonstrates a student communication concept and its temporary-message protocol.",
                        color = AboutGrey,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Designed for smarter and more temporary class communication.",
                color = AboutGrey,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AboutNavy,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Back to Settings",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = AboutNavy,
        fontSize = 19.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun InfoCard(
    title: String,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "•",
                color = AboutTeal,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Column {
                Text(
                    text = title,
                    color = AboutNavy,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = description,
                    color = AboutGrey,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun TechnologyRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = AboutGrey,
            fontSize = 13.sp
        )

        Text(
            text = value,
            color = AboutNavy,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
