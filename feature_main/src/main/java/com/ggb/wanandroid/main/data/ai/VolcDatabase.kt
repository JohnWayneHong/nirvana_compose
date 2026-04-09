package com.ggb.wanandroid.main.data.ai

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ChatSessionEntity::class], version = 1, exportSchema = false)
abstract class VolcDatabase : RoomDatabase() {
    abstract fun chatSessionDao(): ChatSessionDao

    companion object {
        @Volatile
        private var INSTANCE: VolcDatabase? = null

        fun getDatabase(context: Context): VolcDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VolcDatabase::class.java,
                    "volc_ai_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
