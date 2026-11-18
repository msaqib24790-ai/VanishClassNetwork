
package com.vanish.classnetwork.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query(
        """
        SELECT * FROM messages
        WHERE classNetworkId = :classNetworkId
        AND expiresAt IS NOT NULL
        AND expiresAt > :currentTime
        AND status NOT IN ('EXPIRED', 'DROPPED')
        ORDER BY COALESCE(sentAt, createdAt) ASC
        """
    )
    fun observeActiveMessages(
        classNetworkId: String,
        currentTime: Long
    ): Flow<List<MessageEntity>>

    @Query(
        """
        SELECT * FROM messages
        WHERE classNetworkId = :classNetworkId
        AND status = 'WAITING'
        AND expiresAt IS NOT NULL
        AND expiresAt > :currentTime
        ORDER BY createdAt ASC
        """
    )
    fun observeQueuedMessages(
        classNetworkId: String,
        currentTime: Long
    ): Flow<List<MessageEntity>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessage(message: MessageEntity): Long

    // Retrieve an existing message to prevent duplicate insertion.
    @Query(
        """
        SELECT * FROM messages
        WHERE id = :messageId
        AND classNetworkId = :classNetworkId
        LIMIT 1
        """
    )
    suspend fun getMessageById(
        messageId: String,
        classNetworkId: String
    ): MessageEntity?

    // Update message timestamps and status using relay confirmation.
    @Query(
        """
        UPDATE messages
        SET sentAt = :sentAt,
            expiresAt = :expiresAt,
            status = :status
        WHERE id = :messageId
        AND classNetworkId = :classNetworkId
        """
    )
    suspend fun updateRelayMetadata(
        messageId: String,
        classNetworkId: String,
        sentAt: Long,
        expiresAt: Long,
        status: String
    ): Int

    @Query(
        """
        UPDATE messages
        SET status = :newStatus
        WHERE id = :messageId
        AND classNetworkId = :classNetworkId
        AND (expiresAt IS NULL OR expiresAt > :currentTime)
        """
    )
    suspend fun updateStatus(
        messageId: String,
        classNetworkId: String,
        newStatus: String,
        currentTime: Long
    ): Int

    @Query(
        """
        UPDATE messages
        SET status = 'DROPPED'
        WHERE status = 'WAITING'
        AND expiresAt IS NOT NULL
        AND expiresAt <= :currentTime
        """
    )
    suspend fun markExpiredQueuedMessages(
        currentTime: Long
    ): Int

    @Query(
        """
        DELETE FROM messages
        WHERE expiresAt IS NOT NULL
        AND expiresAt <= :currentTime
        """
    )
    suspend fun deleteExpiredMessages(
        currentTime: Long
    ): Int

    @Query(
        """
        DELETE FROM messages
        WHERE id = :messageId
        AND classNetworkId = :classNetworkId
        """
    )
    suspend fun deleteMessage(
        messageId: String,
        classNetworkId: String
    )

    // Remove a message when the relay reports its expiration.
    @Query(
        """
        DELETE FROM messages
        WHERE id = :messageId
        AND classNetworkId = :classNetworkId
        """
    )
    suspend fun deleteExpiredRelayMessage(
        messageId: String,
        classNetworkId: String
    )

    @Query(
        """
        DELETE FROM messages
        WHERE classNetworkId = :classNetworkId
        """
    )
    suspend fun deleteClassMessages(
        classNetworkId: String
    )

    @Query(
        """
        SELECT COUNT(*) FROM messages
        WHERE classNetworkId = :classNetworkId
        """
    )
    suspend fun getMessageCount(
        classNetworkId: String
    ): Int
}