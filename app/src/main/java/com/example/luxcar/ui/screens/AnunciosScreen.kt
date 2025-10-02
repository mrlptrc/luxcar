package com.example.luxcar.ui.screens

import android.R.id.bold
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.luxcar.data.database.AppDatabase
import com.example.luxcar.data.model.Car
import com.example.luxcar.data.model.Poster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnunciosScreen(db: AppDatabase, onLogout: () -> Unit, onOpenCar: (Int) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var posters by remember { mutableStateOf(listOf<Poster>()) }
    val cars by db.carDao().getAllCars().collectAsState(initial = emptyList())

    // buscar do banco quando abrir
    LaunchedEffect(Unit) {
        posters = db.posterDao().list()
    }

    // controle do filtro
    var searchQuery by remember { mutableStateOf("") }

    // controle do Dialog
    var showDialog by remember { mutableStateOf(false) }
    var editingPoster by remember { mutableStateOf<Poster?>(null) }

    // filtro aplicado
    val filteredPosters = posters.filter { poster ->
        val car = cars.find { it.id == poster.carId }
        val textToSearch = "${poster.titulo} ${car?.marca} ${car?.modelo}".lowercase()
        textToSearch.contains(searchQuery.lowercase())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ver Veículos") },
                actions = {
                    TextButton(onClick = { onLogout() }) {
                        Text("Logout", color = MaterialTheme.colorScheme.onSecondary)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingPoster = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
            }
        }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar por modelo ou marca") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 colunas
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredPosters) { poster ->
                    val car = cars.find { it.id == poster.carId }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clickable{
                                onOpenCar(poster.carId);
                            },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            if (poster.imagem.isNotEmpty()) {
                                val bitmap = BitmapFactory.decodeByteArray(poster.imagem, 0, poster.imagem.size)
                                bitmap?.let {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp)
                                    )
                                }
                            }
                            Text(
                                poster.titulo,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1
                            )
                            car?.let {
                                Text("${it.marca} ${it.modelo} (${it.ano})", style = MaterialTheme.typography.bodyMedium)
                            }
                            Text("R$ ${poster.preco}", style = MaterialTheme.typography.bodyLarge)

                            Spacer(Modifier.height(8.dp))

                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextButton(onClick = {
                                    editingPoster = poster
                                    showDialog = true
                                }) { Text("Editar") }

                                TextButton(onClick = {
                                    scope.launch {
                                        db.posterDao().delete(poster)
                                        car?.let { db.carDao().deleteCar(it) }
                                        posters = db.posterDao().list()
                                        Toast.makeText(context, "Anúncio excluído", Toast.LENGTH_SHORT).show()
                                    }
                                }) { Text("Excluir") }
                            }
                        }
                    }
                }
            }

        }
    }

    if (showDialog) {
        AddEditDialog(
            db = db,
            posterToEdit = editingPoster,
            onDismiss = { showDialog = false },
            onSave = {
                scope.launch {
                    posters = db.posterDao().list()
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun AddEditDialog(
    db: AppDatabase,
    posterToEdit: Poster? = null,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var cor by remember { mutableStateOf("") }
    var ano by remember { mutableStateOf("") }
    var kilometragem by remember { mutableStateOf("") }
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<ByteArray?>(null) }

    // preencher se for edição
    LaunchedEffect(posterToEdit) {
        posterToEdit?.let { poster ->
            val car = db.carDao().getCarById(poster.carId)
            car?.let {
                marca = it.marca
                modelo = it.modelo
                cor = it.cor
                ano = it.ano.toString()
                kilometragem = it.kilometragem.toString()
            }
            titulo = poster.titulo
            descricao = poster.descricao
            preco = poster.preco.toString()
            selectedImage = poster.imagem
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                selectedImage = withContext(Dispatchers.IO) {
                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                    val buffer = ByteArrayOutputStream()
                    inputStream?.use { stream ->
                        val data = ByteArray(1024)
                        var read: Int
                        while (stream.read(data).also { read = it } != -1) {
                            buffer.write(data, 0, read)
                        }
                    }
                    buffer.toByteArray()
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(if (posterToEdit == null) "Novo Anúncio" else "Editar Anúncio") },
        text = {
            Column {
                OutlinedTextField(
                    value = marca,
                    onValueChange = { marca = it },
                    label = { Text("Marca") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = modelo,
                    onValueChange = { modelo = it },
                    label = { Text("Modelo") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cor,
                    onValueChange = { cor = it },
                    label = { Text("Cor") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = ano,
                    onValueChange = { ano = it },
                    label = { Text("Ano") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = kilometragem,
                    onValueChange = { kilometragem = it },
                    label = { Text("Km") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = preco,
                    onValueChange = { preco = it },
                    label = { Text("Preço") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Selecionar Imagem")
                }

                selectedImage?.let {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    bitmap?.let { bmp ->
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .padding(top = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                scope.launch {
                    try {
                        if (posterToEdit == null) {
                            // Novo
                            val carId = db.carDao().insertCar(
                                Car(
                                    marca = marca,
                                    modelo = modelo,
                                    cor = cor,
                                    ano = ano.toIntOrNull() ?: 0,
                                    kilometragem = kilometragem.toDoubleOrNull() ?: 0.0
                                )
                            ).toInt()

                            val poster = Poster(
                                titulo = titulo,
                                descricao = descricao,
                                preco = preco.toDoubleOrNull() ?: 0.0,
                                imagem = selectedImage ?: ByteArray(0),
                                carId = carId
                            )
                            db.posterDao().insert(poster)
                        } else {
                            // Editar
                            val car = db.carDao().getCarById(posterToEdit.carId)
                            car?.let {
                                db.carDao().updateCar(
                                    it.copy(
                                        marca = marca,
                                        modelo = modelo,
                                        cor = cor,
                                        ano = ano.toIntOrNull() ?: 0,
                                        kilometragem = kilometragem.toDoubleOrNull() ?: 0.0
                                    )
                                )
                            }

                            db.posterDao().update(
                                posterToEdit.copy(
                                    titulo = titulo,
                                    descricao = descricao,
                                    preco = preco.toDoubleOrNull() ?: 0.0,
                                    imagem = selectedImage ?: ByteArray(0)
                                )
                            )
                        }

                        Toast.makeText(context, "Salvo com sucesso!", Toast.LENGTH_SHORT).show()
                        onSave()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) { Text("Cancelar") }
        }
    )
}

