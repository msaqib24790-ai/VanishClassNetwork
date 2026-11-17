
package com.vanish.classnetwork.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.vanish.classnetwork.data.model.MessageStatus

@Entity(
    tableName = "messages",
    indices = [
        Index(value = ["expiresAt"]),
        Index(value = ["status"]),
        Index(value = ["classNetworkId", "id"], unique = true)
    ]
)
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val classNetworkId: String,
    val sender: String,
    val senderDeviceId: String,
    val text: String,
    val createdAt: Long,
    val sentAt: Long?,
    val expiresAt: Long?,
    val status: MessageStatus,
    val isOutgoing: Boolean
)
