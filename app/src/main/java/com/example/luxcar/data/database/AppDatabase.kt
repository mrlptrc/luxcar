package com.example.luxcar.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.luxcar.data.model.User
import com.example.luxcar.data.model.Car
import com.example.luxcar.data.model.Poster
import com.example.luxcar.data.model.PosterImage

@Database(entities = [User::class, Poster::class, Car::class, PosterImage::class], version = 9, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao // integrando funções da interface
    abstract fun posterDao(): PosterDao // integrando funções da interface
    abstract fun carDao(): CarDao // integrando funções da interface

    abstract fun posterImageDao(): PosterImageDao // integrando funções da interface
}
