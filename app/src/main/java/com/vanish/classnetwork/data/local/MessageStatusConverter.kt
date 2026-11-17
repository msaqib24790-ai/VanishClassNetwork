
package com.vanish.classnetwork.data.local

import androidx.room.TypeConverter
import com.vanish.classnetwork.data.model.MessageStatus

class MessageStatusConverter {

    @TypeConverter
    fun fromStatus(status: MessageStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): MessageStatus {
        return runCatching {
            MessageStatus.valueOf(value)
        }.getOrDefault(MessageStatus.DROPPED)
    }
}
