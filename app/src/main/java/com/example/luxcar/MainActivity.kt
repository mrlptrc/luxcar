package com.example.luxcar

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.luxcar.data.LanguageStore
import com.example.luxcar.data.database.DatabaseBuilder
import com.example.luxcar.data.database.AppDatabase
import com.example.luxcar.data.repository.AuthRepository
import com.example.luxcar.presentation.LoginViewModel
import com.example.luxcar.ui.screens.*
import com.example.luxcar.utils.LocaleManager
import com.example.luxcar.ui.theme.LuxcarTheme
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private lateinit var db: AppDatabase
    private var savedLanguage: String = "pt"
    private var currentScreenState: String = "login"

    fun changeLanguage(lang: String) {
        runBlocking {
            LanguageStore.saveLanguage(this@MainActivity, lang)
        }

        LocaleManager.setLocale(this, lang)

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

        db = DatabaseBuilder.getInstance(applicationContext)

        val initialScreen = savedInstanceState?.getString("currentScreen") ?: "login"

        setContent {
            LuxcarTheme {
                var currentScreen by rememberSaveable { mutableStateOf(initialScreen) }
                this.currentScreenState = currentScreen

                when (currentScreen) {
                    "login" -> LoginScreen(
                        viewModel = LoginViewModel(
                            AuthRepository(db)
                        ),
                        navToRegister = { currentScreen = "cadastro" },
                        navToMain = { currentScreen = "anuncios" },
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
                            val carId = currentScreen.removePrefix("carDetail/").toLong()
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