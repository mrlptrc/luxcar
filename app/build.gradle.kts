import com.google.devtools.ksp.gradle.KspTask

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.luxcar"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.luxcar"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    testOptions {
        animationsDisabled = true

        // Habilita recursos no teste unitário
        unitTests.isIncludeAndroidResources = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"

        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi"
        )
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {

    // CORE
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // COMPOSE + BOM
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // ROOM
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // OUTROS
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("io.coil-kt:coil-compose:2.5.0")

    // ==========================================
    // TESTES UNITÁRIOS
    // ==========================================
    testImplementation("junit:junit:4.13.2")

    // ==========================================
    // TESTES INSTRUMENTADOS (E2E)
    // ==========================================

    // Core do AndroidX Test
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")

    // Espresso Core (para Views tradicionais)
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Espresso Contrib (para RecyclerView, DrawerActions, etc.)
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1") {
        // Evita conflitos com outras dependências
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
    }

    // Espresso Intents (se precisar testar intents)
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")

    // Espresso Web (se precisar testar WebViews)
    // androidTestImplementation("androidx.test.espresso:espresso-web:3.5.1")

    // Compose Test (para componentes Compose)
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Kotlin Coroutines Test (para testar coroutines)
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}