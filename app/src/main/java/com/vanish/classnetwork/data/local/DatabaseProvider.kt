package com.vanish.classnetwork.data.local


import android.content.Context
import com.vanish.classnetwork.data.repository.MessageRepository

object DatabaseProvider {

    @Volatile
    private var database: VanishDatabase? = null

    @Volatile
    private var repository: MessageRepository? = null

    fun getDatabase(context: Context): VanishDatabase {

        return database ?: synchronized(this) {

            database ?: VanishDatabase
                .getDatabase(context.applicationContext)
                .also {
                    database = it
                }
        }
    }

    fun getMessageRepository(context: Context): MessageRepository {

        return repository ?: synchronized(this) {

            repository ?: MessageRepository(
                getDatabase(context).messageDao()
            ).also {
                repository = it
            }
        }
    }
}
