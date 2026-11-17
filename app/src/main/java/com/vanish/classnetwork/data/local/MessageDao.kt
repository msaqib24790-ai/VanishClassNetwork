
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
