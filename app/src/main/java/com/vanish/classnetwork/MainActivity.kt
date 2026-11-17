
package com.vanish.classnetwork

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vanish.classnetwork.connectivity.rememberInternetConnectivity
import com.vanish.classnetwork.data.local.DatabaseProvider
import com.vanish.classnetwork.data.model.Message
import com.vanish.classnetwork.data.repository.StudentIdentityRepository
import com.vanish.classnetwork.screens.about.AboutVanishScreen
import com.vanish.classnetwork.screens.chat.ChatMessage
import com.vanish.classnetwork.screens.chat.ChatScreen
import com.vanish.classnetwork.screens.chat.MessageDetailsScreen
import com.vanish.classnetwork.screens.connection.ConnectionDashboardScreen
import com.vanish.classnetwork.screens.help.HelpTroubleshootingScreen
import com.vanish.classnetwork.screens.join.JoinClassScreen
import com.vanish.classnetwork.screens.privacy.PrivacySecurityScreen
import com.vanish.classnetwork.screens.protocol.ProtocolInfoScreen
import com.vanish.classnetwork.screens.queue.OfflineQueueScreen
import com.vanish.classnetwork.screens.queue.QueuedMessage
import com.vanish.classnetwork.screens.registration.StudentRegistrationScreen
import com.vanish.classnetwork.screens.settings.SettingsScreen
import com.vanish.classnetwork.screens.welcome.WelcomeScreen
import com.vanish.classnetwork.ui.theme.VanishClassNetworkTheme
import com.vanish.classnetwork.viewmodel.MessageViewModel
import com.vanish.classnetwork.viewmodel.MessageViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val AppNavy = Color(0xFF071B2C)
private val AppTeal = Color(0xFF00B596)
private val AppGrey = Color(0xFF64748B)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VanishClassNetworkTheme {
                VanishApp()
            }
        }
    }
}

@Composable
fun VanishApp() {

    val context = LocalContext.current
    val internetAvailable = rememberInternetConnectivity()

    // Student identity repository
    val identityRepository = remember(context) {
        StudentIdentityRepository(context.applicationContext)
    }

    // Room database repository
    val messageRepository = remember(context) {
        DatabaseProvider.getMessageRepository(context.applicationContext)
    }

    // Message ViewModel
    val messageViewModel: MessageViewModel = viewModel(
        factory = remember(messageRepository) {
            MessageViewModelFactory(messageRepository)
        }
    )

    // Observe database messages
    val activeEntities by messageViewModel.activeMessages.collectAsState()
    val queuedEntities by messageViewModel.queuedMessages.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    // Observe saved student identity
    val savedIdentity by identityRepository.studentIdentity.collectAsState(
        initial = null
    )

    // Navigation state
    var currentScreen by remember { mutableStateOf("welcome") }
    var studentName by remember { mutableStateOf("") }
    var classId by remember { mutableStateOf("") }
    var accessCode by remember { mutableStateOf("") }
    var protocolReturnScreen by remember { mutableStateOf("privacy") }
    var previewConnected by remember { mutableStateOf(false) }

    // Selected message for Message Details
    var selectedMessageId by remember {
        mutableStateOf<String?>(null)
    }

    // Start observing Room messages when a class is selected
    LaunchedEffect(classId) {
        if (classId.isNotBlank()) {
            messageViewModel.startObserving(classId)
        }
    }

    // Convert active database messages for ChatScreen
    val chatMessages = activeEntities.map { entity ->

        val remainingMillis = (
                (entity.expiresAt ?: 0L) - System.currentTimeMillis()
                ).coerceAtLeast(0L)

        ChatMessage(
            id = entity.id,
            sender = entity.sender,
            text = entity.text,
            remainingSeconds = (
                    (remainingMillis + 999L) / 1000L
                    ).toInt(),
            isMine = entity.isOutgoing
        )
    }

    // Convert queued database messages for OfflineQueueScreen
    val databaseQueuedMessages = queuedEntities.map { entity ->

        val expiryTime = entity.expiresAt
            ?: (entity.createdAt + 30_000L)

        val remaining = (
                (expiryTime - System.currentTimeMillis())
                ).coerceAtLeast(0L) / 1000L

        QueuedMessage(
            id = entity.id,
            text = entity.text,
            queuedAt = SimpleDateFormat(
                "hh:mm a",
                Locale.getDefault()
            ).format(Date(entity.createdAt)),
            remainingSeconds = remaining.toInt(),
            status = "Waiting for connection"
        )
    }

    // Find the selected message in Room
    val selectedMessage = activeEntities.firstOrNull {
        it.id == selectedMessageId
    }

    // Restore saved student name
    LaunchedEffect(savedIdentity) {
        savedIdentity?.let { identity ->
            studentName = identity.displayName
        }
    }

    // App navigation and Android Back handling
    BackHandler(enabled = currentScreen != "welcome") {

        currentScreen = when (currentScreen) {

            "identity" -> "welcome"

            "join" -> "identity"

            "connection" -> "join"

            "chat" -> "connection"

            "details" -> {
                if (selectedMessageId != null) {
                    selectedMessageId = null
                    "chat"
                } else {
                    "connection"
                }
            }

            "dashboard" -> "connection"

            "queue" -> "connection"

            "settings" -> "connection"

            "privacy" -> "settings"

            "protocol" -> protocolReturnScreen

            "about" -> "settings"

            "help" -> "connection"

            else -> "welcome"
        }
    }

    when (currentScreen) {

        // SCREEN 1: Welcome
        "welcome" -> {

            WelcomeScreen(
                onGetStarted = {
                    currentScreen = if (studentName.isNotBlank()) {
                        "join"
                    } else {
                        "identity"
                    }
                },
                onJoinClass = {
                    currentScreen = "join"
                }
            )
        }

        // SCREEN 2: Student Registration
        "identity" -> {

            StudentRegistrationScreen(
                onContinue = { name ->

                    coroutineScope.launch {
                        try {
                            val identity =
                                identityRepository.saveIdentity(name)

                            studentName = identity.displayName
                            currentScreen = "join"

                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }
                    }
                },
                onBack = {
                    currentScreen = "welcome"
                }
            )
        }

        // SCREEN 3: Join Class
        "join" -> {

            JoinClassScreen(
                studentName = studentName,
                onJoin = { enteredClassId, enteredAccessCode ->

                    classId = enteredClassId
                    accessCode = enteredAccessCode
                    currentScreen = "connection"
                },
                onBack = {
                    currentScreen = "identity"
                }
            )
        }

        // SCREEN 4: Connection Pending
        "connection" -> {

            DestinationScreen(
                title = "Connection Pending",
                description = "Your class details have been entered. Real class verification and relay communication are not connected yet. Explore the chat, message details, connection dashboard, offline queue, settings and help.",
                buttonText = "Open Chat Preview",
                onBack = {
                    currentScreen = "join"
                },
                onContinue = {
                    previewConnected = false
                    currentScreen = "chat"
                },

                secondaryButtonText = "Preview Message Details",
                onSecondary = {
                    selectedMessageId = null
                    currentScreen = "details"
                },
                tertiaryButtonText = "Connection Dashboard",
                onTertiary = {
                    currentScreen = "dashboard"
                },
                quaternaryButtonText = "Offline Message Queue",
                onQuaternary = {
                    currentScreen = "queue"
                },
                fifthButtonText = "Settings",
                onFifth = {
                    currentScreen = "settings"
                },
                sixthButtonText = "Help & Troubleshooting",
                onSixth = {
                    currentScreen = "help"
                }
            )
        }

        // SCREEN 5: Class Chat
        "chat" -> {

            ChatScreen(
                studentName = studentName,
                className = classId.ifBlank { "My Class" },
                isConnected = previewConnected,
                messages = chatMessages,

                onSendMessage = { messageText ->

                    if (
                        messageText.isNotBlank() &&
                        classId.isNotBlank()
                    )

                    {

                        coroutineScope.launch {

                            try {

                                val now = System.currentTimeMillis()

                                val deviceId =
                                    identityRepository.getOrCreateDeviceId()

                                val newMessage = Message(
                                    sender = studentName,
                                    senderDeviceId = deviceId,
                                    text = messageText.trim(),
                                    createdAt = now,
                                    sentAt = null,
                                    expiresAt = now + 30_000L
                                )

                                messageViewModel.saveMessage(
                                    message = newMessage,
                                    classNetworkId = classId,
                                    isOutgoing = true
                                )

                            } catch (exception: Exception) {
                                exception.printStackTrace()
                            }
                        }
                    }
                },

                onBack = {
                    currentScreen = "connection"
                },

                // Open actual message details
                onMessageClick = { messageId ->

                    selectedMessageId = messageId
                    currentScreen = "details"
                }
            )
        }

        // SCREEN 6: Message Details

        // SCREEN 6: Message Details
        "details" -> {

            if (selectedMessage != null) {

                val now = System.currentTimeMillis()
                val expiryTime = selectedMessage.expiresAt

                val remainingMillis = expiryTime?.let {
                    (it - now).coerceAtLeast(0L)
                } ?: 0L

                val remainingSeconds =
                    ((remainingMillis + 999L) / 1000L).toInt()

                MessageDetailsScreen(
                    messageId = selectedMessage.id,
                    sender = selectedMessage.sender,
                    messageText = selectedMessage.text,

                    sentAt = selectedMessage.sentAt?.let {
                        SimpleDateFormat(
                            "dd MMM yyyy, hh:mm:ss a",
                            Locale.getDefault()
                        ).format(Date(it))
                    } ?: "Not recorded",

                    expiresAt = expiryTime?.let {
                        SimpleDateFormat(
                            "dd MMM yyyy, hh:mm:ss a",
                            Locale.getDefault()
                        ).format(Date(it))
                    } ?: "Not set",

                    remainingSeconds = remainingSeconds,
                    deliveryStatus = selectedMessage.status.name,

                    onBack = {
                        selectedMessageId = null
                        currentScreen = "chat"
                    }
                )

            } else if (selectedMessageId != null) {

                // An actual selected message expired or was removed.
                // Never replace its content with sample message content.
                MessageDetailsScreen(
                    messageId = selectedMessageId!!,
                    sender = "Unavailable",
                    messageText = "This message has expired or is no longer available.",
                    sentAt = "No longer available",
                    expiresAt = "No longer available",
                    remainingSeconds = 0,
                    deliveryStatus = "EXPIRED / REMOVED",
                    onBack = {
                        selectedMessageId = null
                        currentScreen = "chat"
                    }
                )

            } else {

                // Preview is shown only when the user explicitly opens it.
                MessageDetailsScreen(
                    messageId = "preview-message-001",
                    sender = studentName.ifBlank {
                        "Sample Student"
                    },
                    messageText = "This is a sample message to demonstrate the Message Details screen.",
                    sentAt = "Preview only",
                    expiresAt = "Not calculated",
                    remainingSeconds = 30,
                    deliveryStatus = "Local preview",
                    onBack = {
                        selectedMessageId = null
                        currentScreen = "connection"
                    }
                )
            }
        }

        // SCREEN 7: Connection Dashboard
        "dashboard" -> {

            ConnectionDashboardScreen(
                studentName = studentName,
                classNetworkId = classId,
                connectionStatus = if (internetAvailable) "Internet available (relay not connected)" else "No internet",
                relayStatus = "Not configured",
                lastHeartbeat = "Not available",
                onRefresh = {
                    // Real network refresh will be implemented later.
                },
                onBack = {
                    currentScreen = "connection"
                }
            )
        }

        // SCREEN 8: Offline Message Queue
        "queue" -> {

            OfflineQueueScreen(
                isOnline = false,
                queuedMessages = databaseQueuedMessages,
                onRetry = {
                    // Real retry logic requires relay connectivity.
                },
                onRemoveExpired = {
                    messageViewModel.removeExpiredMessages()
                },
                onBack = {
                    currentScreen = "connection"
                }
            )
        }

        // SCREEN 9: Settings
        "settings" -> {

            SettingsScreen(
                onBack = {
                    currentScreen = "connection"
                },
                onPrivacy = {
                    currentScreen = "privacy"
                },
                onAbout = {
                    currentScreen = "about"
                }
            )
        }

        // SCREEN 10: Privacy & Security
        "privacy" -> {

            PrivacySecurityScreen(
                onBack = {
                    currentScreen = "settings"
                },
                onProtocol = {
                    protocolReturnScreen = "privacy"
                    currentScreen = "protocol"
                }
            )
        }

        // SCREEN 11: Protocol Information
        "protocol" -> {

            ProtocolInfoScreen(
                onBack = {
                    currentScreen = protocolReturnScreen
                }
            )
        }

        // SCREEN 12: About Vanish
        "about" -> {

            AboutVanishScreen(
                onBack = {
                    currentScreen = "settings"
                }
            )
        }

        // SCREEN 13: Help & Troubleshooting
        "help" -> {

            HelpTroubleshootingScreen(
                onBack = {
                    currentScreen = "connection"
                },
                onProtocol = {
                    protocolReturnScreen = "help"
                    currentScreen = "protocol"
                }
            )
        }
    }
}

@Composable
fun DestinationScreen(
    title: String,
    description: String,
    buttonText: String = "Back",
    onBack: () -> Unit,
    onContinue: (() -> Unit)? = null,
    secondaryButtonText: String? = null,
    onSecondary: (() -> Unit)? = null,
    tertiaryButtonText: String? = null,
    onTertiary: (() -> Unit)? = null,
    quaternaryButtonText: String? = null,
    onQuaternary: (() -> Unit)? = null,
    fifthButtonText: String? = null,
    onFifth: (() -> Unit)? = null,
    sixthButtonText: String? = null,
    onSixth: (() -> Unit)? = null
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = title,
                color = AppNavy,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = description,
                color = AppGrey,
                fontSize = 15.sp,
                lineHeight = 23.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 1. Open Chat Preview
            Button(
                onClick = {
                    if (onContinue != null) {
                        onContinue()
                    } else {
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B1726),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = buttonText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            // 2. Preview Message Details
            if (secondaryButtonText != null && onSecondary != null) {

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppTeal,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = secondaryButtonText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 3. Connection Dashboard
            if (tertiaryButtonText != null && onTertiary != null) {

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onTertiary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEBF7F5),
                        contentColor = AppNavy
                    )
                ) {
                    Text(
                        text = tertiaryButtonText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 4. Offline Message Queue
            if (quaternaryButtonText != null && onQuaternary != null) {

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onQuaternary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF475569),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = quaternaryButtonText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 5. Settings
            if (fifthButtonText != null && onFifth != null) {

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onFifth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEDE7F6),
                        contentColor = AppNavy
                    )
                ) {
                    Text(
                        text = fifthButtonText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 6. Help & Troubleshooting
            if (sixthButtonText != null && onSixth != null) {

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onSixth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEFF2F7),
                        contentColor = AppNavy
                    )
                ) {
                    Text(
                        text = sixthButtonText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (onContinue != null) {

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onBack) {
                    Text(
                        text = "Back",
                        color = AppNavy,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

