package com.example.luxcar.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.luxcar.data.model.User
import com.example.luxcar.data.model.Car
import com.example.luxcar.data.model.Converters
import com.example.luxcar.data.model.Poster
import com.example.luxcar.data.model.PosterImage

@Database(
    entities = [
        User::class,
        Car::class,
        Poster::class,
        PosterImage::class
    ],
    version = 12,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun posterDao(): PosterDao

    abstract fun carDao(): CarDao

    abstract fun posterImageDao(): PosterImageDao
}