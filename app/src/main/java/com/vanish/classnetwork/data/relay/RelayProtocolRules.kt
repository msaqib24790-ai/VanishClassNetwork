package com.vanish.classnetwork.data.relay

object RelayProtocolRules {
    const val VERSION = 1
    const val MESSAGE_TTL_MS = 30_000L
    const val HEARTBEAT_INTERVAL_MS = 15_000L
    const val PONG_TIMEOUT_MS = 30_000L

    val RECONNECT_DELAYS_MS = longArrayOf(1_000L, 2_000L, 4_000L, 8_000L)

    fun isValidJoin(deviceId: String, name: String): Boolean =
        deviceId.isNotBlank() && name.isNotBlank() && name.length <= 30

    fun isValidWelcome(protocol: Int, serverTime: Long): Boolean =
        protocol == VERSION && serverTime > 0L

    fun isValidMessage(id: String, sender: String, text: String): Boolean =
        id.isNotBlank() && sender.isNotBlank() && text.length in 1..500

    fun isValidReceivedMessage(
        id: String,
        sender: String,
        text: String,
        sentAt: Long,
        expiresAt: Long
    ): Boolean =
        isValidMessage(id, sender, text) &&
        sentAt > 0L &&
        sentAt <= Long.MAX_VALUE - MESSAGE_TTL_MS &&
        expiresAt == sentAt + MESSAGE_TTL_MS

    fun isValidDelivered(id: String, count: Int): Boolean =
        id.isNotBlank() && count >= 1

    fun isValidExpired(id: String): Boolean =
        id.isNotBlank()

    fun serverTimeToLocal(serverTime: Long, serverClockOffset: Long): Long =
        serverTime - serverClockOffset
}
