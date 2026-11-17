
package com.vanish.classnetwork.data.model

import java.util.UUID

enum class MessageStatus {
    CREATED,
    WAITING,
    SENT,
    DELIVERED,
    DISPLAYED,
    EXPIRED,
    DROPPED
}

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val sender: String,
    val senderDeviceId: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis(),
    val sentAt: Long? = null,
    val expiresAt: Long? = null,
    val status: MessageStatus = MessageStatus.CREATED
) {
    val remainingMillis: Long
        get() {
            val expiry = expiresAt ?: return 0L
            return (expiry - System.currentTimeMillis()).coerceAtLeast(0L)
        }

    val isExpired: Boolean
        get() = expiresAt != null &&
                System.currentTimeMillis() >= expiresAt

    val remainingSeconds: Int
        get() = ((remainingMillis + 999L) / 1000L).toInt()
}
