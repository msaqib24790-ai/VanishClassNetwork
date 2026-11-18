
package com.vanish.classnetwork.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanish.classnetwork.data.local.MessageEntity
import com.vanish.classnetwork.data.model.Message
import com.vanish.classnetwork.data.model.MessageStatus
import com.vanish.classnetwork.data.repository.MessageRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MessageViewModel(
    private val repository: MessageRepository
) : ViewModel() {

    private val _activeMessages =
        MutableStateFlow<List<MessageEntity>>(emptyList())

    val activeMessages: StateFlow<List<MessageEntity>> =
        _activeMessages.asStateFlow()

    private val _queuedMessages =
        MutableStateFlow<List<MessageEntity>>(emptyList())

    val queuedMessages: StateFlow<List<MessageEntity>> =
        _queuedMessages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var activeMessagesJob: Job? = null
    private var queuedMessagesJob: Job? = null
    private var cleanupJob: Job? = null

    // Observe active messages, queued messages and expiry cleanup.
    fun startObserving(classNetworkId: String) {

        if (classNetworkId.isBlank()) {
            _errorMessage.value = "Class network ID cannot be empty."
            return
        }

        activeMessagesJob?.cancel()
        queuedMessagesJob?.cancel()
        cleanupJob?.cancel()

        activeMessagesJob = viewModelScope.launch {
            repository.observeActiveMessages(classNetworkId)
                .collectLatest { messages ->
                    _activeMessages.value = messages
                }
        }

        queuedMessagesJob = viewModelScope.launch {
            repository.observeQueuedMessages(classNetworkId)
                .collectLatest { messages ->
                    _queuedMessages.value = messages
                }
        }

        cleanupJob = viewModelScope.launch {
            while (true) {
                try {
                    repository.cleanExpiredMessages()
                } catch (exception: Exception) {
                    _errorMessage.value =
                        exception.message ?: "Message cleanup failed."
                }

                delay(1000L)
            }
        }
    }

    // Save a locally created message.
    fun saveMessage(
        message: Message,
        classNetworkId: String,
        isOutgoing: Boolean,
        onSaved: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                repository.saveMessage(
                    message = message,
                    classNetworkId = classNetworkId,
                    isOutgoing = isOutgoing
                )

                // Called only after the database save succeeds.
                onSaved?.invoke()

            } catch (exception: Exception) {
                _errorMessage.value =
                    exception.message ?: "Unable to save message."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Update a message status when permitted by the repository.
    fun updateMessageStatus(
        messageId: String,
        classNetworkId: String,
        status: MessageStatus
    ) {
        viewModelScope.launch {
            try {
                repository.updateMessageStatus(
                    messageId = messageId,
                    classNetworkId = classNetworkId,
                    status = status
                )
            } catch (exception: Exception) {
                _errorMessage.value =
                    exception.message ?: "Unable to update message status."
            }
        }
    }

    // Delete a specific message.
    fun deleteMessage(
        messageId: String,
        classNetworkId: String
    ) {
        viewModelScope.launch {
            try {
                repository.deleteMessage(
                    messageId = messageId,
                    classNetworkId = classNetworkId
                )
            } catch (exception: Exception) {
                _errorMessage.value =
                    exception.message ?: "Unable to delete message."
            }
        }
    }

    // Process a message received from the relay.
    fun processRelayMessage(
        messageId: String,
        classNetworkId: String,
        sender: String,
        senderDeviceId: String,
        text: String,
        sentAt: Long,
        expiresAt: Long
    ) {
        viewModelScope.launch {
            try {
                repository.saveRelayMessage(
                    messageId = messageId,
                    classNetworkId = classNetworkId,
                    sender = sender,
                    senderDeviceId = senderDeviceId,
                    text = text,
                    sentAt = sentAt,
                    expiresAt = expiresAt
                )
            } catch (exception: Exception) {
                _errorMessage.value =
                    exception.message ?: "Unable to process received message."
            }
        }
    }

    // Process a delivery acknowledgement from the relay.
    fun processRelayDelivered(
        messageId: String,
        classNetworkId: String,
        serverTime: Long
    ) {
        viewModelScope.launch {
            try {
                repository.markRelayMessageDelivered(
                    messageId = messageId,
                    classNetworkId = classNetworkId,
                    serverTime = serverTime
                )
            } catch (exception: Exception) {
                _errorMessage.value =
                    exception.message ?: "Unable to update delivery confirmation."
            }
        }
    }

    // Process a relay expiry event.
    fun processRelayExpired(
        messageId: String,
        classNetworkId: String
    ) {
        viewModelScope.launch {
            try {
                repository.removeRelayExpiredMessage(
                    messageId = messageId,
                    classNetworkId = classNetworkId
                )
            } catch (exception: Exception) {
                _errorMessage.value =
                    exception.message ?: "Unable to process message expiry."
            }
        }
    }

    // Run message cleanup when requested by the UI.
    fun removeExpiredMessages() {
        viewModelScope.launch {
            try {
                repository.cleanExpiredMessages()
            } catch (exception: Exception) {
                _errorMessage.value =
                    exception.message ?: "Unable to remove expired messages."
            }
        }
    }

    // Clear the displayed error.
    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        activeMessagesJob?.cancel()
        queuedMessagesJob?.cancel()
        cleanupJob?.cancel()
        super.onCleared()
    }
}
