package com.example.luxcar.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.luxcar.data.model.User
import com.example.luxcar.data.model.Car
import com.example.luxcar.data.model.Poster

@Database(entities = [User::class, Poster::class, Car::class], version = 8, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao // integrando funções da interface
    abstract fun posterDao(): PosterDao // integrando funções da interface
    abstract fun carDao(): CarDao // integrando funções da interface
}

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS cars (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                marca TEXT NOT NULL,
                modelo TEXT NOT NULL,
                cor TEXT NOT NULL,
                ano INTEGER NOT NULL,
                kilometragem REAL NOT NULL
            )
        """.trimIndent())
    }
}
