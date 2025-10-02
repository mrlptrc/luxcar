package com.example.luxcar.data.database

import android.content.Context
import androidx.room.Room

class DatabaseBuilder {
    private var instance: AppDatabase? = null

    fun GetInstance(context: Context): AppDatabase {
        if(instance == null){
            instance = Room.databaseBuilder(
            context.applicationContext, // passando o contexto do application que o database builder precisa
            AppDatabase::class.java, // passa as classes model da nossa aplicação pro databaseBuilder
            "luxcar.db" // nome do arquivo da database
            ).build() // dps que passa os parametros da build com os parametros
        }
        return instance!!
    }
}