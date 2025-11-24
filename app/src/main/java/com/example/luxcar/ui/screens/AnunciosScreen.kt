package com.example.luxcar.ui.screens

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.luxcar.data.database.AppDatabase
import com.example.luxcar.data.model.Car
import com.example.luxcar.data.model.Poster
import com.example.luxcar.data.model.PosterImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.luxcar.R
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnunciosScreen(
    db: AppDatabase,
    onLogout: () -> Unit,
    onOpenCar: (Long) -> Unit,
    onAbout: () -> Unit
) {
    val context = LocalContext.current
    var logoResId = R.drawable.normalgroup

    val scope = rememberCoroutineScope()

    var posters by remember { mutableStateOf(listOf<Poster>()) }
    val cars by db.carDao().getAllCars().collectAsState(initial = emptyList())
    var imagesMap by remember { mutableStateOf(mapOf<Int, ByteArray?>()) } // posterId -> primeira imagem
    var currentScreen by remember { mutableStateOf("login") }
    // buscar do banco quando abrir
    LaunchedEffect(Unit) {
        posters = db.posterDao().list()

        // pegar a primeira imagem de cada poster
        val map = mutableMapOf<Int, ByteArray?>()
        posters.forEach { poster ->
            val imgs = db.posterImageDao().getByPosterId(poster.id)
            map[poster.id] = imgs.firstOrNull()?.image
        }
        imagesMap = map
    }

    // controle do filtro
    var searchQuery by remember { mutableStateOf("") }

    // controle do Dialog
    var showDialog by remember { mutableStateOf(false) }
    var editingPoster by remember { mutableStateOf<Poster?>(null) }

    // filtro
    val filteredPosters = posters.filter { poster ->
        val car = cars.find { it.id.toLong() == poster.carId }
        val textToSearch = "${poster.titulo} ${car?.marca} ${car?.modelo}".lowercase()
        textToSearch.contains(searchQuery.lowercase())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.weight(1f))

                        Image(
                            painter = painterResource(id = logoResId),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .height(40.dp)
                        )

                        Spacer(modifier = Modifier.weight(1f))
                    }
                },
                actions = {
                    TextButton(onClick = { onLogout() }) {
                        Text("Logout", color = Color.Black)
                    }

                    TextButton(onClick = { onAbout() }) {
                        Text("Sobre Nós", color = Color.Black)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingPoster = null
                showDialog = true
            }, containerColor = Color(0xFFFF9800)) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_input_add),
                    contentDescription = "Adicionar"
                )
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar por modelo ou marca") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredPosters) { poster ->
                    val car = cars.find { it.id.toLong() == poster.carId }
                    val cover = imagesMap[poster.id]

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clickable { onOpenCar(poster.carId) },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            cover?.let { imgBytes ->
                                val bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.size)
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
                                Text(
                                    "${it.marca} ${it.modelo} (${it.ano})",
                                    style = MaterialTheme.typography.bodyMedium
                                )
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
                                        imagesMap = imagesMap - poster.id
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
        CustomCarDialog(
            db = db,
            posterToEdit = editingPoster,
            onDismiss = { showDialog = false },
            onSave = { car, poster, images ->
                val ctx = context
                scope.launch {
                    try {
                        if (editingPoster == null) {
                            val newCarIdLong = db.carDao().insertCar(car)
                            val newCarId = newCarIdLong.toInt()

                            val posterToInsert = poster.copy(carId = newCarId.toLong())
                            val newPosterId = db.posterDao().insert(posterToInsert).toInt()

                            images.forEach { img ->
                                db.posterImageDao().insert(
                                    PosterImage(
                                        posterId = newPosterId,
                                        image = img
                                    )
                                )
                            }
                        } else {
                            val ep = editingPoster
                            if (ep != null) {
                                val carToUpdate = car.copy(id = ep.carId.toInt())
                                db.carDao().updateCar(carToUpdate)

                                val posterToUpdate = poster.copy(id = ep.id, carId = ep.carId)
                                db.posterDao().update(posterToUpdate)

                                db.posterImageDao().deleteByPosterId(ep.id)
                                images.forEach { img ->
                                    db.posterImageDao().insert(
                                        PosterImage(
                                            posterId = ep.id,
                                            image = img
                                        )
                                    )
                                }
                            }
                        }

                        posters = db.posterDao().list()
                        val map = mutableMapOf<Int, ByteArray?>()
                        posters.forEach { p ->
                            val imgs = db.posterImageDao().getByPosterId(p.id)
                            map[p.id] = imgs.firstOrNull()?.image
                        }
                        imagesMap = map

                        showDialog = false
                        editingPoster = null
                    } catch (e: Exception) {
                        Toast.makeText(ctx, "Erro ao salvar: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomCarDialog(
    db: AppDatabase,
    posterToEdit: Poster? = null,
    onDismiss: () -> Unit,
    onSave: (Car, Poster, List<ByteArray>) -> Unit
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
    var combustivel by remember { mutableStateOf("Gasolina") }
    var categoria by remember { mutableStateOf("SUV") }
    val acessoriosSelecionados = remember { mutableStateListOf<String>() }
    var selectedImages by remember { mutableStateOf(listOf<ByteArray>()) }
    val Orange = Color(0xFFFF9800)

    val tiposCombustivel = listOf("Gasolina", "Álcool", "Diesel", "Flex", "Elétrico")
    val tiposCategoria = listOf("Sedan", "Hatch", "SUV", "Picape", "Minivan")
    val acessorios = listOf("Ar-condicionado", "ABS", "Airbag", "Câmera de Ré")

    LaunchedEffect(posterToEdit) {
        posterToEdit?.let { poster ->
            val car = db.carDao().getCarById(poster.carId)
            car?.let {
                marca = it.marca
                modelo = it.modelo
                cor = it.cor
                ano = it.ano.toString()
                kilometragem = it.kilometragem.toString()
                combustivel = it.combustivel
                categoria = it.categoria
                acessoriosSelecionados.clear()
                acessoriosSelecionados.addAll(it.acessorios)
            }
            titulo = poster.titulo
            descricao = poster.descricao
            preco = poster.preco.toString()

            val imgs: List<PosterImage> = db.posterImageDao().getByPosterId(poster.id)
            selectedImages = imgs.map { it.image }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        scope.launch {
            val images = uris.mapNotNull { uri ->
                withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                }
            }
            selectedImages = selectedImages + images
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        )
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color(0xFF1A1A1A),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (posterToEdit == null) "Novo Cadastro" else "Editar Cadastro",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )

                @Composable
                fun outlinedTextField(
                    value: String,
                    onValueChange: (String) -> Unit,
                    labelText: String,
                    placeholderText: String,
                    keyboardType: KeyboardType = KeyboardType.Text
                ) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        label = { Text(labelText, color = Color.White) },
                        placeholder = { Text(placeholderText, color = Color.LightGray) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
                    )
            }

                outlinedTextField(marca, { marca = it }, "Marca", "Digite a marca")
                outlinedTextField(modelo, { modelo = it }, "Modelo", "Digite o modelo")
                outlinedTextField(cor, { cor = it }, "Cor", "Digite a cor")
                outlinedTextField(ano, { ano = it }, "Ano", "Digite o ano", KeyboardType.Number)
                outlinedTextField(kilometragem, { kilometragem = it }, "Km", "Digite a quilometragem", KeyboardType.Number)
                outlinedTextField(titulo, { titulo = it }, "Título", "Digite o título")
                outlinedTextField(descricao, { descricao = it }, "Descrição", "Digite a descrição")
                outlinedTextField(preco, { preco = it }, "Preço", "Digite o preço", KeyboardType.Number)

                var expandedCategoria by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedCategoria,
                    onExpandedChange = { expandedCategoria = !expandedCategoria }
                ) {
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoria", color = Color.White) },
                        placeholder = { Text("Selecione uma categoria", color = Color.LightGray) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategoria,
                        onDismissRequest = { expandedCategoria = false }
                    ) {
                        tiposCategoria.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    categoria = tipo
                                    expandedCategoria = false
                                }
                            )
                        }
                    }
                }

                var expandedCombustivel by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedCombustivel,
                    onExpandedChange = { expandedCombustivel = !expandedCombustivel }
                ) {
                    OutlinedTextField(
                        value = combustivel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Combustível", color = Color.White) },
                        placeholder = { Text("Selecione o combustível", color = Color.LightGray) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCombustivel) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCombustivel,
                        onDismissRequest = { expandedCombustivel = false }
                    ) {
                        tiposCombustivel.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    combustivel = tipo
                                    expandedCombustivel = false
                                }
                            )
                        }
                    }
                }

                Text("Acessórios", color = Color.White)
                acessorios.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (acessoriosSelecionados.contains(item)) acessoriosSelecionados.remove(item)
                                else acessoriosSelecionados.add(item)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item, color = Color.White)
                        Checkbox(
                            checked = acessoriosSelecionados.contains(item),
                            onCheckedChange = {
                                if (it) acessoriosSelecionados.add(item)
                                else acessoriosSelecionados.remove(item)
                            }
                        )
                    }
                }

                Button(
                    colors = ButtonDefaults.buttonColors(Orange),
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Selecionar Imagens") }

                if (selectedImages.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(selectedImages) { imgBytes ->
                            val bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.size)
                            bmp?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.size(100.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        colors = ButtonDefaults.buttonColors(Orange),
                        onClick = {
                            val car = Car(
                                marca = marca,
                                modelo = modelo,
                                cor = cor,
                                ano = ano.toIntOrNull() ?: 0,
                                kilometragem = kilometragem.toDoubleOrNull() ?: 0.0,
                                combustivel = combustivel,
                                categoria = categoria,
                                acessorios = acessoriosSelecionados.toList()
                            )
                            val poster = Poster(
                                titulo = titulo,
                                descricao = descricao,
                                preco = preco.toDoubleOrNull() ?: 0.0,
                                imagem = selectedImages.firstOrNull() ?: byteArrayOf(),
                                carId = posterToEdit?.carId ?: 0
                            )
                            onSave(car, poster, selectedImages)
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Salvar") }

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancelar", color = Color.Red) }
                }
            }
        }
    }
}







