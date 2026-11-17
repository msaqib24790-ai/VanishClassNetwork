
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

    suspend fun updateMessageStatus(
        messageId: String,
        classNetworkId: String,
        status: MessageStatus
    ) {

        require(status != MessageStatus.SENT &&
                status != MessageStatus.DELIVERED &&
                status != MessageStatus.DISPLAYED) {
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
