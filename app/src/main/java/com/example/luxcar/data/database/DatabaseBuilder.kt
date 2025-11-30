package com.example.luxcar.data.database

import android.content.Context
import androidx.room.Room

object DatabaseBuilder {

    @Volatile
    private var instance: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            instance ?: buildDatabase(context).also { instance = it }
        }
    }
    private fun buildDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "luxcar.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    fun clearInstance() {
        instance?.close()
        instance = null
    }
}