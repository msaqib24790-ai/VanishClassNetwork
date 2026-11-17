package com.vanish.classnetwork.data.local


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [MessageEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(MessageStatusConverter::class)
abstract class VanishDatabase : RoomDatabase() {

    abstract fun messageDao(): MessageDao

    companion object {

        @Volatile
        private var INSTANCE: VanishDatabase? = null

        fun getDatabase(context: Context): VanishDatabase {

            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    VanishDatabase::class.java,
                    "vanish_temporary.db"
                )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
