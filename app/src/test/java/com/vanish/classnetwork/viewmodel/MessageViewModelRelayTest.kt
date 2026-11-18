package com.vanish.classnetwork.viewmodel

import com.vanish.classnetwork.data.local.MessageDao
import com.vanish.classnetwork.data.local.MessageEntity
import com.vanish.classnetwork.data.model.MessageStatus
import com.vanish.classnetwork.data.repository.MessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MessageViewModelRelayTest {

    private val dao = RecordingMessageDao()
    private lateinit var viewModel: MessageViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = MessageViewModel(MessageRepository(dao))
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun incomingRelayMessage_isInserted() = runTest {
        viewModel.processRelayMessage(
            messageId = "incoming-1",
            classNetworkId = "class-1",
            sender = "TestSender",
            senderDeviceId = "device-1",
            text = "Incoming test message",
            sentAt = 1000L,
            expiresAt = 31000L
        )

        assertNotNull("Incoming message should be inserted", dao.insertedMessage)
        assertEquals(1, dao.insertCount)
    }

    @Test
    fun relayDeliveryAcknowledgement_updatesMessageStatus() = runTest {
        viewModel.processRelayDelivered(
            messageId = "message-1",
            classNetworkId = "class-1",
            serverTime = 2000L
        )

        assertEquals("message-1", dao.updatedMessageId)
        assertEquals("class-1", dao.updatedClassId)
        assertEquals(MessageStatus.DELIVERED.name, dao.updatedStatus)
    }

    @Test
    fun relayExpiryEvent_deletesMessage() = runTest {
        viewModel.processRelayExpired(
            messageId = "expired-1",
            classNetworkId = "class-1"
        )

        assertEquals("expired-1", dao.deletedMessageId)
        assertEquals("class-1", dao.deletedClassId)
    }

    private class RecordingMessageDao : MessageDao {
        var insertedMessage: MessageEntity? = null
        var insertCount = 0
        var updatedMessageId: String? = null
        var updatedClassId: String? = null
        var updatedStatus: String? = null
        var deletedMessageId: String? = null
        var deletedClassId: String? = null

        override fun observeActiveMessages(
            classNetworkId: String,
            currentTime: Long
        ): Flow<List<MessageEntity>> = flowOf(emptyList())

        override fun observeQueuedMessages(
            classNetworkId: String,
            currentTime: Long
        ): Flow<List<MessageEntity>> = flowOf(emptyList())

        override suspend fun insertMessage(message: MessageEntity): Long {
            insertedMessage = message
            insertCount++
            return 1L
        }

        override suspend fun getMessageById(
            messageId: String,
            classNetworkId: String
        ): MessageEntity? = null

        override suspend fun updateRelayMetadata(
            messageId: String,
            classNetworkId: String,
            sentAt: Long,
            expiresAt: Long,
            status: String
        ): Int = 1

        override suspend fun updateStatus(
            messageId: String,
            classNetworkId: String,
            newStatus: String,
            currentTime: Long
        ): Int {
            updatedMessageId = messageId
            updatedClassId = classNetworkId
            updatedStatus = newStatus
            return 1
        }

        override suspend fun markExpiredQueuedMessages(currentTime: Long): Int = 0

        override suspend fun deleteExpiredMessages(currentTime: Long): Int = 0

        override suspend fun deleteMessage(messageId: String, classNetworkId: String) {}

        override suspend fun deleteExpiredRelayMessage(
            messageId: String,
            classNetworkId: String
        ) {
            deletedMessageId = messageId
            deletedClassId = classNetworkId
        }

        override suspend fun deleteClassMessages(classNetworkId: String) {}

        override suspend fun getMessageCount(classNetworkId: String): Int = 0
    }
}


