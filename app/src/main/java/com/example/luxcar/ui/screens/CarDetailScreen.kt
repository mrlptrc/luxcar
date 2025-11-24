package com.example.luxcar.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.luxcar.data.database.AppDatabase
import com.example.luxcar.data.model.Car
import com.example.luxcar.data.model.Poster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailScreen(db: AppDatabase, carId: Int, onBack: () -> Unit, logoResId: Int) {
    var car by remember { mutableStateOf<Car?>(null) }
    var poster by remember { mutableStateOf<Poster?>(null) }
    var images by remember { mutableStateOf(listOf<ByteArray>()) }
    var fontScale by remember { mutableStateOf(1f) } // acessibilidade
    val Orange = Color(0xFFFF9800)

    LaunchedEffect(carId) {
        val loadedCar = withContext(Dispatchers.IO) { db.carDao().getCarById(carId.toLong()) }
        val loadedPoster = withContext(Dispatchers.IO) { db.posterDao().getByCarId(carId) }
        val loadedImages = loadedPoster?.let {
            withContext(Dispatchers.IO) {
                db.posterImageDao().getByPosterId(it.id).mapNotNull { img -> img.image }
            }
        } ?: emptyList()

        car = loadedCar
        poster = loadedPoster
        images = loadedImages
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Detalhes do Carro",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = MaterialTheme.typography.titleLarge.fontSize * fontScale)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_media_previous),
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { fontScale += 0.1f }, colors = ButtonDefaults.buttonColors(containerColor = Orange)) { Text("A+") }
                Button(onClick = { fontScale = (fontScale - 0.1f).coerceAtLeast(0.8f) }, colors = ButtonDefaults.buttonColors(containerColor = Orange)) { Text("A-") }
            }

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
                                    .width(280.dp * fontScale)
                                    .height(180.dp * fontScale),
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

            car?.let { c ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${c.marca} ${c.modelo}", style = MaterialTheme.typography.headlineMedium.copy(fontSize = MaterialTheme.typography.headlineMedium.fontSize * fontScale))
                    Text("${c.ano} • ${c.cor}", style = MaterialTheme.typography.titleLarge.copy(fontSize = MaterialTheme.typography.titleLarge.fontSize * fontScale), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${c.kilometragem} km", style = MaterialTheme.typography.titleMedium.copy(fontSize = MaterialTheme.typography.titleMedium.fontSize * fontScale), color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Badge(text = c.categoria, color = Color(0xFFFF5722), fontScale = fontScale)
                        Badge(text = c.combustivel, color = Color(0xFF4CAF50), fontScale = fontScale)
                    }

                    if (c.acessorios.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Acessórios:", style = MaterialTheme.typography.titleMedium.copy(fontSize = MaterialTheme.typography.titleMedium.fontSize * fontScale))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                c.acessorios.forEach { acc ->
                                    Badge(text = acc, color = Color(0xFF2196F3), fontScale = fontScale)
                                }
                            }
                        }
                    }
                }
            }

            poster?.let { p ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(p.descricao, style = MaterialTheme.typography.titleMedium.copy(fontSize = MaterialTheme.typography.titleMedium.fontSize * fontScale))
                }
            }

            poster?.let { p ->
                Text(
                    "R$ ${p.preco}",
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = MaterialTheme.typography.headlineSmall.fontSize * fontScale),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun Badge(text: String, color: Color, fontScale: Float = 1f) {
    Box(
        modifier = Modifier
            .background(color, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = Color.White, style = MaterialTheme.typography.bodyMedium.copy(fontSize = MaterialTheme.typography.bodyMedium.fontSize * fontScale))
    }
}

