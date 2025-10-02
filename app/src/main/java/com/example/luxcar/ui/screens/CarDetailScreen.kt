package com.example.luxcar.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.luxcar.data.database.AppDatabase
import com.example.luxcar.data.model.Car
import com.example.luxcar.data.model.Poster

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailScreen(db: AppDatabase, carId: Int, onBack: () -> Unit) {
    var car by remember { mutableStateOf<Car?>(null) }
    var poster by remember { mutableStateOf<Poster?>(null) }

    LaunchedEffect(carId) {
        car = db.carDao().getCarById(carId)
        poster = db.posterDao().getByCarId(carId) // precisa criar esse método no DAO
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Carro") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues).padding(16.dp)) {
            poster?.let { p ->
                Text(p.titulo, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(p.descricao)
                Spacer(Modifier.height(8.dp))
                Text("Preço: R$ ${p.preco}")
            }

            Spacer(Modifier.height(16.dp))

            car?.let {
                Text("Marca: ${it.marca}")
                Text("Modelo: ${it.modelo}")
                Text("Ano: ${it.ano}")
                Text("Cor: ${it.cor}")
                Text("Km: ${it.kilometragem}")
            }

            Spacer(Modifier.height(16.dp))

            // Carrossel de imagens (começando pela principal)
            poster?.let { p ->
                if (p.imagem.isNotEmpty()) {
                    val imagens = listOf(p.imagem) // depois trocamos para lista de várias imagens
                    LazyRow {
                        items(imagens) { img ->
                            val bmp = BitmapFactory.decodeByteArray(img, 0, img.size)
                            bmp?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(250.dp)
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
