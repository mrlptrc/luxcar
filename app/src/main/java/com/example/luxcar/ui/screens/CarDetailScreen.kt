package com.example.luxcar.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.luxcar.data.database.AppDatabase
import com.example.luxcar.data.model.Car
import com.example.luxcar.data.model.Poster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailScreen(db: AppDatabase, carId: Int, onBack: () -> Unit) {
    var car by remember { mutableStateOf<Car?>(null) }
    var poster by remember { mutableStateOf<Poster?>(null) }
    var images by remember { mutableStateOf(listOf<ByteArray>()) }

    // carregamento da ui
    LaunchedEffect(carId) {
        withContext(Dispatchers.IO) {
            val loadedCar = db.carDao().getCarById(carId)
            val loadedPoster = db.posterDao().getByCarId(carId)
            val loadedImages = loadedPoster?.let {
                db.posterImageDao().getByPosterId(it.id).mapNotNull { img -> img.image }
            } ?: emptyList()

            car = loadedCar
            poster = loadedPoster
            images = loadedImages
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Carro", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // carrossel
            if (images.isNotEmpty()) {
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(images) { imgBytes ->
                        val bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.size)
                        bmp?.let {
                            Card(
                                modifier = Modifier
                                    .width(280.dp)
                                    .height(180.dp),
                                shape = MaterialTheme.shapes.medium,
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            // infos dos carros
            car?.let { c ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "${c.marca} ${c.modelo}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        "${c.ano} • ${c.cor}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "${c.kilometragem} km",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // outras infos
            poster?.let { p ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        p.descricao,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            poster?.let { p ->
                Text(
                    "R$ ${p.preco}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
