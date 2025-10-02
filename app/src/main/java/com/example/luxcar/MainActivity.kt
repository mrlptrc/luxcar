// MainActivity.kt
package com.example.luxcar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.room.Room
import com.example.luxcar.data.database.AppDatabase
import com.example.luxcar.ui.screens.LoginScreen
import com.example.luxcar.ui.screens.RegisterScreen
import com.example.luxcar.ui.screens.*
import com.example.luxcar.ui.theme.LuxcarTheme

class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // criação do banco com migração
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "luxcar-db"
        )
            .fallbackToDestructiveMigration()
            .build()

        setContent {
            LuxcarTheme {
                var currentScreen by remember { mutableStateOf("login") }

                when (currentScreen) {
                    "login" -> LoginScreen(
                        navToRegister = { currentScreen = "cadastro" },
                        navToMain = { currentScreen = "anuncios" },
                        db = db
                    )
                    "cadastro" -> RegisterScreen(
                        navToLogin = { currentScreen = "login" },
                        db = db
                    )
                    "anuncios" -> AnunciosScreen(
                        db = db,
                        onLogout = { currentScreen = "login" },
                        onOpenCar = { carId -> currentScreen = "carDetail/$carId" }
                    )
                    else -> {
                        if (currentScreen.startsWith("carDetail/")) {
                            val carId = currentScreen.removePrefix("carDetail/").toInt()
                            CarDetailScreen(
                                db = db,
                                carId = carId,
                                onBack = { currentScreen = "anuncios" }
                            )
                        }
                    }
                }
            }
        }
    }
}
