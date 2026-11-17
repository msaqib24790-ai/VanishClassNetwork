
package com.vanish.classnetwork.screens.welcome

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val IMAGE_TAG = "VANISH_IMAGES"

private val Navy = Color(0xFF071B2C)
private val LightText = Color(0xFFF4FBFD)

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onJoinClass: () -> Unit
) {
    val context = LocalContext.current

    val welcomeBitmap: Bitmap? = remember {
        loadAssetImage(context, "branding/welcome_screen.png")
    }

    val logoBitmap: Bitmap? = remember {
        loadAssetImage(context, "logo/vanish_logo.png")
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            if (welcomeBitmap != null) {
                Image(
                    bitmap = welcomeBitmap.asImageBitmap(),
                    contentDescription = "Vanish Welcome Screen",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            } else {
                Text(
                    text = "Welcome screen image could not be loaded",
                    modifier = Modifier.align(Alignment.Center),
                    color = Navy
                )
            }

            if (logoBitmap != null) {
                Image(
                    bitmap = logoBitmap.asImageBitmap(),
                    contentDescription = "Vanish Class Network Logo",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 35.dp)
                        .size(85.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(
                    text = "LOGO NOT LOADED",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 35.dp),
                    color = Color.Red
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(
                        start = 35.dp,
                        end = 35.dp,
                        bottom = 75.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .width(280.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Navy,
                        contentColor = LightText
                    )
                ) {
                    Text(
                        text = "Get Started",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightText
                    )
                }

                OutlinedButton(
                    onClick = onJoinClass,
                    modifier = Modifier
                        .width(280.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = Color.Black
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Join Your Class",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

private fun loadAssetImage(
    context: android.content.Context,
    path: String
): Bitmap? {
    return try {
        context.assets.open(path).use { stream ->
            val bitmap = BitmapFactory.decodeStream(stream)

            if (bitmap == null) {
                Log.e(IMAGE_TAG, "Could not decode image: $path")
            } else {
                Log.d(
                    IMAGE_TAG,
                    "Loaded $path successfully: ${bitmap.width} x ${bitmap.height}"
                )
            }

            bitmap
        }
    } catch (exception: Exception) {
        Log.e(IMAGE_TAG, "Failed to load image: $path", exception)
        null
    }
}
