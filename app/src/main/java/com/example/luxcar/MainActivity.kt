package com.example.luxcar

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.room.Room
import com.example.luxcar.data.LanguageStore
import com.example.luxcar.data.database.AppDatabase
import com.example.luxcar.ui.screens.*
import com.example.luxcar.utils.LocaleManager
import com.example.luxcar.ui.theme.LuxcarTheme
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private lateinit var db: AppDatabase
    private var savedLanguage: String = "pt"
    private var currentScreenState: String = "login"

    // --- FUNÇÃO QUE MUDA O IDIOMA ---
    fun changeLanguage(lang: String) {
        // salva idioma no DataStore
        runBlocking {
            LanguageStore.saveLanguage(this@MainActivity, lang)
        }

        // aplica o locale
        LocaleManager.setLocale(this, lang)

        // recria activity mantendo "currentScreen"
        recreate()
    }

    override fun attachBaseContext(newBase: Context?) {
        if (newBase != null) {
            val lang = runBlocking { LanguageStore.loadLanguage(newBase) }
            savedLanguage = lang

            val context = LocaleManager.setLocale(newBase, lang)
            super.attachBaseContext(context)
        } else {
            super.attachBaseContext(newBase)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- inicializa banco ---
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "luxcar-db"
        )
            .fallbackToDestructiveMigration()
            .build()

        // restaura tela atual
        val initialScreen = savedInstanceState?.getString("currentScreen") ?: "login"

        setContent {
            LuxcarTheme {
                var currentScreen by rememberSaveable { mutableStateOf(initialScreen) }
                this.currentScreenState = currentScreen

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
                        onOpenCar = { carId -> currentScreen = "carDetail/$carId" },
                        onAbout = { currentScreen = "sobremim" },
                        onLanguageChange = { lang -> changeLanguage(lang) }
                    )
                    "sobremim" -> AboutScreen(
                        logoResId = R.drawable.normalgroup,
                        onBack = { currentScreen = "anuncios" }
                    )
                    else -> {
                        if (currentScreen.startsWith("carDetail/")) {
                            val carId = currentScreen.removePrefix("carDetail/").toInt()
                            CarDetailScreen(
                                db = db,
                                carId = carId,
                                onBack = { currentScreen = "anuncios" },
                                logoResId = R.drawable.normalgroup
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("currentScreen", currentScreenState)
        super.onSaveInstanceState(outState)
    }
}
