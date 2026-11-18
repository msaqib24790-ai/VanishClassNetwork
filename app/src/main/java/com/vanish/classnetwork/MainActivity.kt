
package com.vanish.classnetwork

import com.vanish.classnetwork.data.relay.RelayProtocolRules

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
import androidx.compose.runtime.DisposableEffect
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
import com.vanish.classnetwork.data.relay.RelayWebSocketClient
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

private const val RELAY_MESSAGE_LIFETIME = 30_000L

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

    val identityRepository = remember(context) {
        StudentIdentityRepository(context.applicationContext)
    }

    val messageRepository = remember(context) {
        DatabaseProvider.getMessageRepository(context.applicationContext)
    }

    val messageViewModel: MessageViewModel = viewModel(
        factory = remember(messageRepository) {
            MessageViewModelFactory(messageRepository)
        }
    )

    val activeEntities by messageViewModel.activeMessages.collectAsState()
    val queuedEntities by messageViewModel.queuedMessages.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val attemptedQueuedMessageIds = remember {
        mutableSetOf<String>()
    }

    var queueRetryRevision by remember {
        mutableStateOf(0)
    }

    val savedIdentity by identityRepository.studentIdentity.collectAsState(
        initial = null
    )

    // Navigation state
    var currentScreen by remember { mutableStateOf("welcome") }
    var studentName by remember { mutableStateOf("") }
    var classId by remember { mutableStateOf("") }
    var accessCode by remember { mutableStateOf("") }
    var protocolReturnScreen by remember { mutableStateOf("privacy") }

    var selectedMessageId by remember {
        mutableStateOf<String?>(null)
    }

    // Relay state
    var relayConnected by remember {
        mutableStateOf(false)
    }

    var relayError by remember {
        mutableStateOf<String?>(null)
    }

    var relayClockOffset by remember {
        mutableStateOf(0L)
    }

    // ---------------------------------------------------------
    // REAL WEBSOCKET RELAY CONNECTION
    // ---------------------------------------------------------

    val relayClient = remember {

        RelayWebSocketClient(

            onConnected = { serverTime ->

                coroutineScope.launch {

                    attemptedQueuedMessageIds.clear()

                    relayConnected = true
                    relayError = null

                    relayClockOffset =
                        serverTime - System.currentTimeMillis()
                }
            },

            onDisconnected = { reason ->

                coroutineScope.launch {

                    attemptedQueuedMessageIds.clear()

                    relayConnected = false
                    relayError = reason
                }
            },

            onMessage = { frame ->

                coroutineScope.launch {

                    try {

                        when (frame.optString("type")) {

                            // ---------------------------------
                            // MESSAGE RECEIVED OR OUTGOING ECHO
                            // ---------------------------------

                            "message" -> {

                                val messageId = frame.optString("id")
                                val sender = frame.optString("sender")
                                val messageText = frame.optString("text")

                                val serverSentAt =
                                    frame.optLong("sent_at", 0L)

                                val serverExpiresAt =
                                    frame.optLong("expires_at", 0L)

                                val validMessage =
                                    RelayProtocolRules.isValidReceivedMessage(id = messageId, sender = sender, text = messageText, sentAt = serverSentAt, expiresAt = serverExpiresAt) && classId.isNotBlank()

                                if (!validMessage) {

                                    relayError =
                                        "Invalid relay message frame received."

                                    return@launch
                                }

                                // Convert relay timestamps to phone time.
                                val localSentAt =
                                    RelayProtocolRules.serverTimeToLocal(serverSentAt, relayClockOffset)

                                val localExpiresAt =
                                    RelayProtocolRules.serverTimeToLocal(serverExpiresAt, relayClockOffset)

                                // Ignore messages that have already expired.
                                if (
                                    localExpiresAt >
                                    System.currentTimeMillis()
                                ) {

                                    messageViewModel.processRelayMessage(
                                        messageId = messageId,
                                        classNetworkId = classId,
                                        sender = sender,

                                        // The relay protocol does not provide
                                        // sender device IDs.
                                        senderDeviceId = "relay-unknown",

                                        text = messageText,
                                        sentAt = localSentAt,
                                        expiresAt = localExpiresAt
                                    )
                                }
                            }

                            // ---------------------------------
                            // DELIVERY ACKNOWLEDGEMENT
                            // ---------------------------------

                            "delivered" -> {

                                val messageId = frame.optString("id")
                                val recipientCount =
                                    frame.optInt("count", -1)

                                if (RelayProtocolRules.isValidDelivered(messageId, recipientCount) && classId.isNotBlank()) {

                                    messageViewModel.processRelayDelivered(
                                        messageId = messageId,
                                        classNetworkId = classId,

                                        // The delivered frame has no
                                        // server_time field.
                                        serverTime =
                                            System.currentTimeMillis()
                                    )

                                } else {

                                    relayError =
                                        "Invalid delivery acknowledgement received."
                                }
                            }

                            // ---------------------------------
                            // MESSAGE EXPIRY EVENT
                            // ---------------------------------

                            "expired" -> {

                                val messageId = frame.optString("id")

                                if (RelayProtocolRules.isValidExpired(messageId) && classId.isNotBlank()) {

                                    messageViewModel.processRelayExpired(
                                        messageId = messageId,
                                        classNetworkId = classId
                                    )

                                } else {

                                    relayError =
                                        "Invalid expiry event received."
                                }
                            }

                            else -> {
                                relayError =
                                    "Unsupported relay event received."
                            }
                        }

                    } catch (exception: Exception) {

                        relayError =
                            exception.message
                                ?: "Unable to process relay event."
                    }
                }
            },

            onError = { error ->

                coroutineScope.launch {
                    relayError = error
                }
            }
        )
    }

    // ---------------------------------------------------------
    // CONNECT WHEN CLASS AND IDENTITY ARE AVAILABLE
    // ---------------------------------------------------------

    LaunchedEffect(classId, savedIdentity) {

        if (classId.isNotBlank()) {
            messageViewModel.startObserving(classId)
        }

        val identity = savedIdentity

        if (classId.isNotBlank() && identity != null) {

            try {

                relayClient.disconnect()

                attemptedQueuedMessageIds.clear()

                relayConnected = false
                relayError = null

                val deviceId =
                    identityRepository.getOrCreateDeviceId()

                relayClient.connect(
                    deviceId = deviceId,
                    displayName = identity.displayName
                )

            } catch (exception: Exception) {

                relayConnected = false

                relayError =
                    exception.message ?: "Could not connect to relay."
            }
        }
    }

    // ---------------------------------------------------------
    // AUTOMATIC OFFLINE QUEUE RETRANSMISSION
    // ---------------------------------------------------------

    LaunchedEffect(
        relayConnected,
        classId,
        queuedEntities,
        queueRetryRevision
    ) {

        if (!relayConnected || classId.isBlank()) {
            return@LaunchedEffect
        }

        val now = System.currentTimeMillis()

        queuedEntities
            .filter { entity ->

                entity.isOutgoing &&
                        entity.status.name == "WAITING" &&
                        entity.id !in attemptedQueuedMessageIds
            }
            .forEach { entity ->

                val expiryTime = entity.expiresAt
                    ?: (entity.createdAt + RELAY_MESSAGE_LIFETIME)

                if (expiryTime <= now) {
                    return@forEach
                }

                attemptedQueuedMessageIds.add(entity.id)

                val accepted = relayClient.sendMessage(
                    id = entity.id,
                    sender = entity.sender,
                    text = entity.text
                )

                if (!accepted) {

                    relayError =
                        "A queued message could not be transmitted. Retry when the relay is available."
                }
            }
    }

    // ---------------------------------------------------------
    // DISCONNECT WHEN COMPOSITION IS REMOVED
    // ---------------------------------------------------------

    DisposableEffect(relayClient) {

        onDispose {
            relayClient.disconnect()
        }
    }

    // ---------------------------------------------------------
    // DATABASE MESSAGE CONVERSION
    // ---------------------------------------------------------

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

    val databaseQueuedMessages = queuedEntities.map { entity ->

        val expiryTime = entity.expiresAt
            ?: (entity.createdAt + RELAY_MESSAGE_LIFETIME)

        val remaining = (
                expiryTime - System.currentTimeMillis()
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

    val selectedMessage = activeEntities.firstOrNull {
        it.id == selectedMessageId
    }

    // Restore saved student name.
    LaunchedEffect(savedIdentity) {

        savedIdentity?.let { identity ->
            studentName = identity.displayName
        }
    }

    // ---------------------------------------------------------
    // ANDROID BACK HANDLING
    // ---------------------------------------------------------

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

    // ---------------------------------------------------------
    // SCREEN NAVIGATION
    // ---------------------------------------------------------

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

        // SCREEN 4: Connection
        "connection" -> {

            DestinationScreen(

                title = "Connection Pending",

                description = if (relayConnected) {
                    "Your app has successfully connected to the relay. Open chat to continue."
                } else {
                    "Your class details have been entered. The app is attempting to establish a real relay connection."
                },

                buttonText = "Open Chat",

                onBack = {
                    currentScreen = "join"
                },

                onContinue = {
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

                className = classId.ifBlank {
                    "My Class"
                },

                isConnected = relayConnected,

                messages = chatMessages,

                onSendMessage = { messageText ->

                    if (
                        messageText.isNotBlank() &&
                        classId.isNotBlank()
                    ) {

                        coroutineScope.launch {

                            try {

                                val now = System.currentTimeMillis()

                                val deviceId =
                                    identityRepository.getOrCreateDeviceId()

                                val trimmedText = messageText.trim()

                                if (trimmedText.length > 500) {
                                    relayError =
                                        "Messages cannot exceed 500 characters."
                                    return@launch
                                }

                                val newMessage = Message(
                                    sender = studentName,
                                    senderDeviceId = deviceId,
                                    text = trimmedText,
                                    createdAt = now,
                                    sentAt = null,
                                    expiresAt =
                                        now + RELAY_MESSAGE_LIFETIME
                                )

                                messageViewModel.saveMessage(
                                    message = newMessage,
                                    classNetworkId = classId,
                                    isOutgoing = true,

                                    onSaved = {

                                        attemptedQueuedMessageIds.add(
                                            newMessage.id
                                        )

                                        val sent = relayClient.sendMessage(
                                            id = newMessage.id,
                                            sender = studentName,
                                            text = trimmedText
                                        )

                                        relayError = if (sent) {
                                            null
                                        } else {
                                            "Message saved locally, but relay transmission failed."
                                        }
                                    }
                                )

                            } catch (exception: Exception) {

                                exception.printStackTrace()

                                relayError =
                                    exception.message
                                        ?: "Unable to send message."
                            }
                        }
                    }
                },

                onBack = {
                    currentScreen = "connection"
                },

                onMessageClick = { messageId ->

                    selectedMessageId = messageId
                    currentScreen = "details"
                }
            )
        }

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

                MessageDetailsScreen(

                    messageId = selectedMessageId!!,
                    sender = "Unavailable",

                    messageText =
                        "This message has expired or is no longer available.",

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

                MessageDetailsScreen(

                    messageId = "preview-message-001",

                    sender = studentName.ifBlank {
                        "Sample Student"
                    },

                    messageText =
                        "This is a sample message to demonstrate the Message Details screen.",

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

                connectionStatus = when {
                    relayConnected -> "Connected to relay"
                    !internetAvailable -> "No internet connection"
                    else -> "Connecting to relay"
                },

                relayStatus = when {
                    relayConnected -> "Connected"
                    relayError != null -> "Connection error: $relayError"
                    else -> "Connecting / Disconnected"
                },

                lastHeartbeat = if (relayConnected) {
                    "Heartbeat active"
                } else {
                    "Not available"
                },

                onRefresh = {
                    // Manual relay refresh remains pending.
                },

                onBack = {
                    currentScreen = "connection"
                }
            )
        }

        // SCREEN 8: Offline Message Queue
        "queue" -> {

            OfflineQueueScreen(

                isOnline = relayConnected,
                queuedMessages = databaseQueuedMessages,

                onRetry = {

                    if (relayConnected) {

                        attemptedQueuedMessageIds.clear()
                        queueRetryRevision++

                    } else {

                        relayError =
                            "Waiting for relay connection."
                    }
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

// -------------------------------------------------------------
// DESTINATION / NAVIGATION SCREEN
// -------------------------------------------------------------

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



