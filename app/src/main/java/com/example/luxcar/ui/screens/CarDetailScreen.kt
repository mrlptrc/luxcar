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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luxcar.data.database.AppDatabase
import com.example.luxcar.data.model.Car
import com.example.luxcar.data.model.Poster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.luxcar.R

/**
 * Tela de detalhes do carro
 * ✅ CORRIGIDO: Parâmetro carId agora é Long
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailScreen(
    db: AppDatabase,
    carId: Long, // ✅ Long em vez de Int
    onBack: () -> Unit,
    logoResId: Int
) {
    var car by remember { mutableStateOf<Car?>(null) }
    var poster by remember { mutableStateOf<Poster?>(null) }
    var images by remember { mutableStateOf(listOf<ByteArray>()) }
    var fontScale by remember { mutableStateOf(1f) }
    val Orange = Color(0xFFFF9800)
    val LightGray = Color(0xFFF5F5F5)

    val tiposCombustivelMap = mapOf(
        "fuel_gas" to stringResource(R.string.fuel_gas),
        "fuel_alcohol" to stringResource(R.string.fuel_alcohol),
        "fuel_diesel" to stringResource(R.string.fuel_diesel),
        "fuel_flex" to stringResource(R.string.fuel_flex),
        "fuel_electric" to stringResource(R.string.fuel_electric)
    )

    val tiposCategoriaMap = mapOf(
        "cat_sedan" to stringResource(R.string.cat_sedan),
        "cat_hatch" to stringResource(R.string.cat_hatch),
        "cat_suv" to stringResource(R.string.cat_suv),
        "cat_pickup" to stringResource(R.string.cat_pickup),
        "cat_minivan" to stringResource(R.string.cat_minivan)
    )

    val acessoriosMap = mapOf(
        "acc_ac" to stringResource(R.string.acc_ac),
        "acc_abs" to stringResource(R.string.acc_abs),
        "acc_airbag" to stringResource(R.string.acc_airbag),
        "acc_camera" to stringResource(R.string.acc_camera)
    )

    LaunchedEffect(carId) {
        // ✅ CORRIGIDO: Não precisa mais converter para Long
        val loadedCar = withContext(Dispatchers.IO) {
            db.carDao().getCarById(carId)
        }
        val loadedPoster = withContext(Dispatchers.IO) {
            db.posterDao().getByCarId(carId)
        }
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
                    Text(
                        stringResource(id = R.string.car_details_title),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_media_previous),
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // --- galeria de imagens ---
            if (images.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp * fontScale)
                        .background(LightGray)
                ) {
                    LazyRow(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(images) { imgBytes ->
                            val bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.size)
                            bmp?.let {
                                Box(
                                    modifier = Modifier
                                        .fillParentMaxWidth()
                                        .fillMaxHeight()
                                ) {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- conteúdo principal ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {

                // --- controle de fonte ---
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { fontScale += 0.1f },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Orange
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            stringResource(id = R.string.font_increase),
                            fontSize = (14.sp.value * fontScale).sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    OutlinedButton(
                        onClick = { fontScale = (fontScale - 0.1f).coerceAtLeast(0.8f) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Orange
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            stringResource(id = R.string.font_decrease),
                            fontSize = (14.sp.value * fontScale).sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // --- informações principais ---
                car?.let { c ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // título
                        Text(
                            "${c.marca} ${c.modelo}",
                            fontSize = (32.sp.value * fontScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )

                        // ano, cor, km
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            InfoRow(
                                label = stringResource(id = R.string.year),
                                value = c.ano.toString(),
                                fontScale = fontScale
                            )
                            InfoRow(
                                label = stringResource(id = R.string.color),
                                value = c.cor,
                                fontScale = fontScale
                            )
                            InfoRow(
                                label = stringResource(id = R.string.km),
                                value = "${c.kilometragem} km",
                                fontScale = fontScale
                            )
                        }

                        // categoria e combustível
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DetailBadge(
                                text = tiposCategoriaMap[c.categoria] ?: c.categoria,
                                fontScale = fontScale
                            )
                            DetailBadge(
                                text = tiposCombustivelMap[c.combustivel] ?: c.combustivel,
                                fontScale = fontScale
                            )
                        }
                    }
                }

                // --- acessórios ---
                car?.let { c ->
                    if (c.acessorios.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                stringResource(id = R.string.accessories),
                                fontSize = (20.sp.value * fontScale).sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF212121)
                            )

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                c.acessorios.forEach { acc ->
                                    DetailBadge(
                                        text = acessoriosMap[acc] ?: acc,
                                        fontScale = fontScale)
                                }
                            }
                        }
                    }
                }

                // --- descrição ---
                poster?.let { p ->
                    if (p.descricao.isNotBlank()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                stringResource(id = R.string.description),
                                fontSize = (20.sp.value * fontScale).sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF212121)
                            )

                            Text(
                                p.descricao,
                                fontSize = (16.sp.value * fontScale).sp,
                                lineHeight = (24.sp.value * fontScale).sp,
                                color = Color(0xFF616161)
                            )
                        }
                    }
                }

                // --- preço ---
                poster?.let { p ->
                    Divider(
                        color = Color(0xFFE0E0E0),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            stringResource(id = R.string.price_label),
                            fontSize = (14.sp.value * fontScale).sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF757575),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            stringResource(id = R.string.price, p.preco),
                            fontSize = (36.sp.value * fontScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Orange,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// ==========================================================
// COMPONENTES
// ==========================================================

@Composable
fun InfoRow(label: String, value: String, fontScale: Float = 1f) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = (16.sp.value * fontScale).sp,
            color = Color(0xFF757575),
            fontWeight = FontWeight.Normal
        )
        Text(
            value,
            fontSize = (16.sp.value * fontScale).sp,
            color = Color(0xFF212121),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DetailBadge(text: String, fontScale: Float = 1f) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFF424242),
            fontSize = (14.sp.value * fontScale).sp,
            fontWeight = FontWeight.Medium
        )
    }
}