
package com.vanish.classnetwork.data.relay

import android.os.Handler
import android.os.Looper
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class RelayWebSocketClient(
    private val onConnected: (serverTime: Long) -> Unit,
    private val onDisconnected: (reason: String) -> Unit,
    private val onMessage: (JSONObject) -> Unit,
    private val onError: (String) -> Unit
) {

    companion object {
        private const val RELAY_URL =
            "wss://vanish-relay-sawaira.bonto.page/ws"

        private const val HEARTBEAT_INTERVAL = 15_000L
        private const val PONG_TIMEOUT = 30_000L
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(75, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private val handler = Handler(Looper.getMainLooper())

    @Volatile
    private var webSocket: WebSocket? = null

    @Volatile
    private var connected = false

    @Volatile
    private var manuallyDisconnected = false

    @Volatile
    private var awaitingPong = false

    @Volatile
    var serverClockOffset: Long = 0L
        private set

    private var deviceId = ""
    private var displayName = ""
    private var reconnectAttempt = 0

    private var heartbeatRunnable: Runnable? = null
    private var pongTimeoutRunnable: Runnable? = null
    private var reconnectRunnable: Runnable? = null

    @Synchronized
    fun connect(deviceId: String, displayName: String) {
        if (webSocket != null) return

        require(deviceId.isNotBlank()) {
            "Device ID is required"
        }

        require(displayName.isNotBlank() && displayName.length <= 30) {
            "Name must contain 1 to 30 characters"
        }

        this.deviceId = deviceId
        this.displayName = displayName
        manuallyDisconnected = false

        openSocket()
    }

    @Synchronized
    private fun openSocket() {
        if (manuallyDisconnected || webSocket != null) return

        val request = Request.Builder()
            .url(RELAY_URL)
            .build()

        webSocket = client.newWebSocket(
            request,
            object : WebSocketListener() {

                override fun onOpen(
                    webSocket: WebSocket,
                    response: Response
                ) {
                    val joinFrame = JSONObject()
                        .put("type", "join")
                        .put("protocol", 1)
                        .put("device_id", deviceId)
                        .put("name", displayName)

                    val sent = webSocket.send(joinFrame.toString())

                    if (!sent) {
                        handleDisconnect(
                            webSocket,
                            "Failed to send JOIN frame"
                        )
                    }
                }

                override fun onMessage(
                    webSocket: WebSocket,
                    text: String
                ) {
                    try {
                        val frame = JSONObject(text)

                        when (frame.optString("type")) {

                            "welcome" -> {
                                val protocolVersion =
                                    frame.optInt("protocol", -1)

                                val serverTime =
                                    frame.optLong("server_time", 0L)

                                if (!RelayProtocolRules.isValidWelcome(protocolVersion, serverTime)) {
                                    val reason =
                                        "Invalid relay welcome frame"

                                    onError(reason)
                                    webSocket.close(1002, reason)
                                    handleDisconnect(webSocket, reason)
                                    return
                                }

                                serverClockOffset =
                                    serverTime - System.currentTimeMillis()

                                connected = true
                                reconnectAttempt = 0

                                startHeartbeat(webSocket)

                                onConnected(serverTime)
                            }

                            "pong" -> {
                                handler.post {
                                    if (
                                        connected &&
                                        this@RelayWebSocketClient.webSocket === webSocket &&
                                        awaitingPong
                                    ) {
                                        awaitingPong = false
                                        cancelPongTimeout()

                                        heartbeatRunnable?.let {
                                            handler.removeCallbacks(it)
                                            handler.postDelayed(
                                                it,
                                                HEARTBEAT_INTERVAL
                                            )
                                        }
                                    }
                                }
                            }

                            "ping" -> {
                                webSocket.send(
                                    JSONObject()
                                        .put("type", "pong")
                                        .toString()
                                )
                            }

                            "error" -> {
                                onError(
                                    "${frame.optString("code")}: " +
                                            frame.optString("message")
                                )
                            }

                            "message",
                            "delivered",
                            "expired" -> {
                                onMessage(frame)
                            }

                            else -> {
                                onError(
                                    "Unknown relay frame: " +
                                            frame.optString("type")
                                )
                            }
                        }

                    } catch (exception: Exception) {
                        onError(
                            exception.message ?: "Invalid relay response"
                        )
                    }
                }

                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: Response?
                ) {
                    handleDisconnect(
                        webSocket,
                        t.message ?: "Relay connection failed"
                    )
                }

                override fun onClosed(
                    webSocket: WebSocket,
                    code: Int,
                    reason: String
                ) {
                    handleDisconnect(webSocket, reason)
                }
            }
        )
    }

    private fun startHeartbeat(socket: WebSocket) {
        stopHeartbeat()

        heartbeatRunnable = object : Runnable {
            override fun run() {
                if (!connected || webSocket !== socket) return

                if (awaitingPong) return

                awaitingPong = true

                val sent = socket.send(
                    JSONObject()
                        .put("type", "ping")
                        .toString()
                )

                if (!sent) {
                    awaitingPong = false

                    handleDisconnect(
                        socket,
                        "Heartbeat send failed"
                    )
                    return
                }

                pongTimeoutRunnable = Runnable {
                    if (
                        connected &&
                        webSocket === socket &&
                        awaitingPong
                    ) {
                        socket.close(1001, "Pong timeout")

                        handleDisconnect(
                            socket,
                            "Heartbeat timeout"
                        )
                    }
                }

                handler.postDelayed(
                    pongTimeoutRunnable!!,
                    PONG_TIMEOUT
                )
            }
        }

        handler.postDelayed(
            heartbeatRunnable!!,
            HEARTBEAT_INTERVAL
        )
    }

    private fun stopHeartbeat() {
        heartbeatRunnable?.let {
            handler.removeCallbacks(it)
        }

        heartbeatRunnable = null
        awaitingPong = false

        cancelPongTimeout()
    }

    private fun cancelPongTimeout() {
        pongTimeoutRunnable?.let {
            handler.removeCallbacks(it)
        }

        pongTimeoutRunnable = null
    }

    @Synchronized
    private fun handleDisconnect(
        socket: WebSocket,
        reason: String
    ) {
        if (webSocket !== socket) return

        val wasConnected = connected

        connected = false
        webSocket = null

        stopHeartbeat()

        if (wasConnected) {
            onDisconnected(reason)
        }

        scheduleReconnect()
    }

    @Synchronized
    private fun scheduleReconnect() {
        if (manuallyDisconnected || reconnectRunnable != null) return

        val delays = longArrayOf(
            1_000,
            2_000,
            4_000,
            8_000
        )

        val delay = delays[
            reconnectAttempt.coerceAtMost(delays.lastIndex)
        ]

        reconnectAttempt++

        reconnectRunnable = Runnable {
            reconnectRunnable = null
            openSocket()
        }

        handler.postDelayed(
            reconnectRunnable!!,
            delay
        )
    }

    fun sendMessage(
        id: String,
        sender: String,
        text: String
    ): Boolean {
        if (
            !connected ||
            id.isBlank() ||
            sender.isBlank() ||
            text.isBlank() ||
            text.length > 500
        ) {
            return false
        }

        val frame = JSONObject()
            .put("type", "message")
            .put("id", id)
            .put("sender", sender)
            .put("text", text)

        return webSocket?.send(frame.toString()) == true
    }

    @Synchronized
    fun disconnect() {
        manuallyDisconnected = true
        connected = false

        reconnectRunnable?.let {
            handler.removeCallbacks(it)
        }

        reconnectRunnable = null

        stopHeartbeat()

        webSocket?.close(1000, "Client disconnected")
        webSocket = null
    }

    fun isConnected(): Boolean = connected
}

