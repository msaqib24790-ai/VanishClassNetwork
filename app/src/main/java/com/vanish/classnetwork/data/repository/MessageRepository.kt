
package com.vanish.classnetwork.data.repository

import com.vanish.classnetwork.data.local.MessageDao
import com.vanish.classnetwork.data.local.MessageEntity
import com.vanish.classnetwork.data.model.Message
import com.vanish.classnetwork.data.model.MessageStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class MessageRepository(
    private val messageDao: MessageDao
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeActiveMessages(
        classNetworkId: String
    ): Flow<List<MessageEntity>> {

        return flow {
            while (true) {
                emit(System.currentTimeMillis())
                delay(1000L)
            }
        }.flatMapLatest { currentTime ->
            messageDao.observeActiveMessages(
                classNetworkId = classNetworkId,
                currentTime = currentTime
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeQueuedMessages(
        classNetworkId: String
    ): Flow<List<MessageEntity>> {

        return flow {
            while (true) {
                emit(System.currentTimeMillis())
                delay(1000L)
            }
        }.flatMapLatest { currentTime ->
            messageDao.observeQueuedMessages(
                classNetworkId = classNetworkId,
                currentTime = currentTime
            )
        }
    }

    suspend fun saveMessage(
        message: Message,
        classNetworkId: String,
        isOutgoing: Boolean
    ) {

        require(classNetworkId.isNotBlank()) {
            "Class network ID cannot be empty."
        }

        require(message.text.isNotBlank()) {
            "Message cannot be empty."
        }

        require(message.text.length <= 1000) {
            "Message cannot exceed 1000 characters."
        }

        val now = System.currentTimeMillis()

        val expiry = message.expiresAt
            ?: throw IllegalArgumentException(
                "Message expiry time is required."
            )

        require(expiry > now) {
            "Cannot save an already expired message."
        }

        val safeStatus = if (isOutgoing) {
            MessageStatus.WAITING
        } else {
            message.status
        }

        val insertedId = messageDao.insertMessage(
            MessageEntity(
                id = message.id,
                classNetworkId = classNetworkId,
                sender = message.sender,
                senderDeviceId = message.senderDeviceId,
                text = message.text,
                createdAt = message.createdAt,
                sentAt = null,
                expiresAt = expiry,
                status = safeStatus,
                isOutgoing = isOutgoing
            )
        )

        check(insertedId != -1L) {
            "Duplicate message rejected."
        }
    }


    // Process a message received from the relay.
    // Update an existing outgoing message instead of inserting a duplicate.
    suspend fun saveRelayMessage(
        messageId: String,
        classNetworkId: String,
        sender: String,
        senderDeviceId: String,
        text: String,
        sentAt: Long,
        expiresAt: Long
    ): Boolean {
        require(classNetworkId.isNotBlank())
        require(messageId.isNotBlank())
        require(text.isNotBlank() && text.length <= 500)
        require(expiresAt > sentAt)

        val existing = messageDao.getMessageById(
            messageId,
            classNetworkId
        )

        if (existing != null) {
            if (existing.isOutgoing) {
                messageDao.updateRelayMetadata(
                    messageId = messageId,
                    classNetworkId = classNetworkId,
                    sentAt = sentAt,
                    expiresAt = expiresAt,
                    status = MessageStatus.SENT.name
                )
            }
            return false
        }

        val relayMessage = MessageEntity(
            id = messageId,
            classNetworkId = classNetworkId,
            sender = sender,
            senderDeviceId = senderDeviceId,
            text = text,
            createdAt = sentAt,
            sentAt = sentAt,
            expiresAt = expiresAt,
            status = MessageStatus.SENT,
            isOutgoing = false
        )

        return messageDao.insertMessage(relayMessage) != -1L
    }

    // Mark a message as delivered after relay confirmation.
    suspend fun markRelayMessageDelivered(
        messageId: String,
        classNetworkId: String,
        serverTime: Long
    ) {
        messageDao.updateStatus(
            messageId = messageId,
            classNetworkId = classNetworkId,
            newStatus = MessageStatus.DELIVERED.name,
            currentTime = serverTime
        )
    }

    // Delete a message when the relay reports expiration.
    suspend fun removeRelayExpiredMessage(
        messageId: String,
        classNetworkId: String
    ) {
        messageDao.deleteExpiredRelayMessage(
            messageId,
            classNetworkId
        )
    }

    suspend fun updateMessageStatus(
        messageId: String,
        classNetworkId: String,
        status: MessageStatus
    ) {
        require(
            status != MessageStatus.SENT &&
                    status != MessageStatus.DELIVERED &&
                    status != MessageStatus.DISPLAYED
        ) {
            "Delivery statuses require relay confirmation."
        }

        messageDao.updateStatus(
            messageId = messageId,
            classNetworkId = classNetworkId,
            newStatus = status.name,
            currentTime = System.currentTimeMillis()
        )
    }

    suspend fun cleanExpiredMessages(): Int {
        val now = System.currentTimeMillis()

        messageDao.markExpiredQueuedMessages(now)

        return messageDao.deleteExpiredMessages(now)
    }

    suspend fun deleteMessage(
        messageId: String,
        classNetworkId: String
    ) {
        messageDao.deleteMessage(
            messageId,
            classNetworkId
        )
    }

    suspend fun clearClassMessages(
        classNetworkId: String
    ) {
        messageDao.deleteClassMessages(classNetworkId)
    }

    suspend fun getMessageCount(
        classNetworkId: String
    ): Int {
        return messageDao.getMessageCount(classNetworkId)
    }
}
