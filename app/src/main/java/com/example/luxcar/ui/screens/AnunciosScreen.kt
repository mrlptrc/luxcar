package com.example.luxcar.ui.screens

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.luxcar.data.database.AppDatabase
import com.example.luxcar.data.model.Car
import com.example.luxcar.data.model.Poster
import com.example.luxcar.data.model.PosterImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.luxcar.R
import kotlinx.coroutines.withContext
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnunciosScreen(
    db: AppDatabase,
    onLogout: () -> Unit,
    onOpenCar: (Long) -> Unit,
    onAbout: () -> Unit,
    onLanguageChange: (String) -> Unit
) {
    val context = LocalContext.current
    var logoResId = R.drawable.normalgroup
    val scope = rememberCoroutineScope()
    val Orange = Color(0xFFFF9800)

    // --- estados da tela ---
    var posters by remember { mutableStateOf(listOf<Poster>()) }
    val cars by db.carDao().getAllCars().collectAsState(initial = emptyList())
    var imagesMap by remember { mutableStateOf(mapOf<Long, ByteArray?>()) }

    // --- carregamento inicial dos dados do banco ---
    LaunchedEffect(Unit) {
        posters = db.posterDao().list()
        val map = mutableMapOf<Long, ByteArray?>()
        posters.forEach { poster ->
            val imgs = db.posterImageDao().getByPosterId(poster.id)
            map[poster.id] = imgs.firstOrNull()?.image
        }
        imagesMap = map
    }

    // --- estados de filtro e busca ---
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoria by remember { mutableStateOf<String?>(null) }

    // --- estados do dialog de cadastro/edição ---
    var showDialog by remember { mutableStateOf(false) }
    var editingPoster by remember { mutableStateOf<Poster?>(null) }
    var showLangMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var posterToDelete by remember { mutableStateOf<Poster?>(null) }

    // --- lógica de filtro ---
    val filteredPosters by remember(posters, cars, searchQuery, selectedCategoria) {
        derivedStateOf {
            posters.filter { poster ->
                val car = cars.find { it.id.toLong() == poster.carId } ?: return@filter false
                val categoriaMatch = selectedCategoria?.let { car.categoria == it } ?: true
                val textToSearch = "${poster.titulo} ${car.marca} ${car.modelo}".lowercase()
                val searchMatch = textToSearch.contains(searchQuery.lowercase())
                categoriaMatch && searchMatch
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = logoResId),
                            contentDescription = stringResource(R.string.logo_description),
                            modifier = Modifier.height(36.dp)
                        )
                    }
                },
                actions = {
                    // menu idioma
                    IconButton(onClick = { showLangMenu = true }) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_preferences),
                            contentDescription = stringResource(R.string.language),
                            tint = Color(0xFF424242)
                        )
                    }

                    DropdownMenu(
                        expanded = showLangMenu,
                        onDismissRequest = { showLangMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("🇺🇸", fontSize = 20.sp)
                                    Text("English", fontSize = 15.sp)
                                }
                            },
                            onClick = {
                                showLangMenu = false
                                onLanguageChange("en")
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("🇪🇸", fontSize = 20.sp)
                                    Text("Español", fontSize = 15.sp)
                                }
                            },
                            onClick = {
                                showLangMenu = false
                                onLanguageChange("es")
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("🇧🇷", fontSize = 20.sp)
                                    Text("Português", fontSize = 15.sp)
                                }
                            },
                            onClick = {
                                showLangMenu = false
                                onLanguageChange("pt")
                            }
                        )
                    }

                    // sobre
                    TextButton(onClick = onAbout) {
                        Text(
                            stringResource(R.string.about_us),
                            color = Color(0xFF424242),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // logout
                    TextButton(onClick = onLogout) {
                        Text(
                            stringResource(R.string.logout),
                            color = Orange,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingPoster = null
                    showDialog = true
                },
                containerColor = Black,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("create_post_button")
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_input_add),
                    contentDescription = stringResource(R.string.new_ad),
                    tint = Color.White
                )
            }
        },
        containerColor = Color(0xFFFAFAFA)
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // --- campo de busca ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = {
                    Text(
                        stringResource(R.string.search_hint),
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Orange,
                    focusedLabelColor = Orange
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_search),
                        contentDescription = null,
                        tint = Color(0xFF757575)
                    )
                }
            )

            Spacer(Modifier.height(20.dp))

            // --- filtro por categoria ---
            val categoriasKeys = listOf(null, "cat_suv", "cat_hatch", "cat_sedan", "cat_pickup", "cat_minivan")
            val categoriasLabels = listOf(
                stringResource(R.string.cat_all),
                stringResource(R.string.cat_suv),
                stringResource(R.string.cat_hatch),
                stringResource(R.string.cat_sedan),
                stringResource(R.string.cat_pickup),
                stringResource(R.string.cat_minivan)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                categoriasKeys.forEachIndexed { index, key ->
                    val isSelected = selectedCategoria == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) Orange else Color(0xFFF5F5F5)
                            )
                            .clickable { selectedCategoria = key }
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = categoriasLabels[index],
                            color = if (isSelected) Color.White else Color(0xFF424242),
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // --- recyclerview com grid de anúncios ---
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    RecyclerView(context).apply {
                        layoutManager = GridLayoutManager(context, 2)
                        adapter = PosterAdapter(
                            posters = filteredPosters,
                            cars = cars.toMutableList(),
                            images = imagesMap.toMutableMap(),
                            onOpen = { poster -> onOpenCar(poster.carId) },
                            onEdit = { poster ->
                                editingPoster = poster
                                showDialog = true
                            },
                            onDelete = { poster ->
                                posterToDelete = poster
                                showDeleteDialog = true
                            }
                        )
                    }
                },
                update = { recyclerView ->
                    val adapter = recyclerView.adapter as PosterAdapter
                    adapter.updateList(
                        newList = filteredPosters,
                        newImages = imagesMap,
                        newCars = cars,
                    )
                }
            )
        }
    }

    // --- dialog de cadastro/edição ---
    if (showDialog) {
        CustomCarDialog(
            db = db,
            posterToEdit = editingPoster,
            onDismiss = { showDialog = false },
            onSave = { car, poster, images ->
                scope.launch {
                    try {
                        if (editingPoster == null) {
                            val newCarIdLong = db.carDao().insertCar(car)
                            val newCarId = newCarIdLong
                            val posterToInsert = poster.copy(carId = newCarId.toLong())
                            val newPosterId = db.posterDao().insert(posterToInsert)
                            images.forEach { img ->
                                db.posterImageDao().insert(
                                    PosterImage(posterId = newPosterId, image = img)
                                )
                            }
                        } else {
                            editingPoster?.let { ep ->
                                val carToUpdate = car.copy(id = ep.carId)
                                db.carDao().updateCar(carToUpdate)
                                val posterToUpdate = poster.copy(id = ep.id, carId = ep.carId)
                                db.posterDao().update(posterToUpdate)
                                db.posterImageDao().deleteByPosterId(ep.id)
                                images.forEach { img ->
                                    db.posterImageDao().insert(
                                        PosterImage(posterId = ep.id, image = img)
                                    )
                                }
                            }
                        }
                        posters = db.posterDao().list()
                        val map = mutableMapOf<Long, ByteArray?>()
                        posters.forEach { p ->
                            val imgs = db.posterImageDao().getByPosterId(p.id)
                            map[p.id] = imgs.firstOrNull()?.image
                        }
                        imagesMap = map
                        showDialog = false
                        editingPoster = null
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_save, e.message),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        )
    }

    // --- dialog de confirmação de exclusão ---
    if (showDeleteDialog && posterToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    stringResource(R.string.confirm_delete_title),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(stringResource(R.string.confirm_delete_message))
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            posterToDelete?.let { poster ->
                                val car = cars.find { it.id.toLong() == poster.carId }
                                db.posterDao().delete(poster)
                                car?.let { db.carDao().deleteCar(it) }
                                posters = db.posterDao().list()
                                imagesMap = imagesMap - poster.id
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.ad_deleted),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        showDeleteDialog = false
                        posterToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.ad_deleted))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        posterToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// ============================================
// DIALOG DE CADASTRO/EDIÇÃO DE CARRO
// ============================================
// ============================================
// DIALOG DE CADASTRO/EDIÇÃO DE CARRO COM TEST TAGS
// ============================================
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
    val Orange = Color(0xFFFF9800)

    // --- estados dos campos ---
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var cor by remember { mutableStateOf("") }
    var ano by remember { mutableStateOf("") }
    var kilometragem by remember { mutableStateOf("") }
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var combustivel by remember { mutableStateOf("fuel_gas") }
    var categoria by remember { mutableStateOf("cat_suv") }
    var emNegociacao by remember { mutableStateOf(false) }
    val acessoriosSelecionados = remember { mutableStateListOf<String>() }
    var selectedImages by remember { mutableStateOf(listOf<ByteArray>()) }

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

    // --- carregar dados se estiver editando ---
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
            emNegociacao = poster.emNegociacao
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
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- header ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (posterToEdit == null)
                            stringResource(R.string.new_ad)
                        else
                            stringResource(R.string.edit_ad),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("dismiss_dialog_button")) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                            contentDescription = stringResource(R.string.cancel),
                            tint = Color(0xFF757575)
                        )
                    }
                }

                Divider(color = Color(0xFFE0E0E0))

                // --- campos de texto com testTag ---
                @Composable
                fun DialogTextField(
                    value: String,
                    onValueChange: (String) -> Unit,
                    labelResId: Int,
                    placeholderResId: Int,
                    testTag: String,
                    keyboardType: KeyboardType = KeyboardType.Text
                ) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        label = { Text(stringResource(labelResId), fontSize = 14.sp) },
                        placeholder = { Text(stringResource(placeholderResId), fontSize = 14.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(testTag),
                        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Orange,
                            focusedLabelColor = Orange
                        )
                    )
                }

                DialogTextField(marca, { marca = it }, R.string.brand, R.string.hint_brand, "marca_input")
                DialogTextField(modelo, { modelo = it }, R.string.model, R.string.hint_model, "modelo_input")

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.weight(1f)) {
                        DialogTextField(cor, { cor = it }, R.string.color, R.string.hint_color, "cor_input")
                    }
                    Box(Modifier.weight(1f)) {
                        DialogTextField(ano, { ano = it }, R.string.year, R.string.hint_year, "ano_input", KeyboardType.Number)
                    }
                }

                DialogTextField(kilometragem, { kilometragem = it }, R.string.km, R.string.hint_km, "km_input", KeyboardType.Number)
                DialogTextField(titulo, { titulo = it }, R.string.title, R.string.hint_title, "titulo_input")
                DialogTextField(descricao, { descricao = it }, R.string.description, R.string.hint_description, "descricao_input")
                DialogTextField(preco, { preco = it }, R.string.price_label, R.string.hint_price, "preco_input", KeyboardType.Number)

                // --- dropdown categoria ---
                var expandedCategoria by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedCategoria,
                    onExpandedChange = { expandedCategoria = !expandedCategoria }
                ) {
                    OutlinedTextField(
                        value = tiposCategoriaMap[categoria] ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.category), fontSize = 14.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("categoria_dropdown"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Orange,
                            focusedLabelColor = Orange
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategoria,
                        onDismissRequest = { expandedCategoria = false }
                    ) {
                        tiposCategoriaMap.forEach { (key, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    categoria = key
                                    expandedCategoria = false
                                },
                                modifier = Modifier.testTag("categoria_item_$key")
                            )
                        }
                    }
                }

                // --- dropdown combustível ---
                var expandedCombustivel by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedCombustivel,
                    onExpandedChange = { expandedCombustivel = !expandedCombustivel }
                ) {
                    OutlinedTextField(
                        value = tiposCombustivelMap[combustivel] ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.fuel), fontSize = 14.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCombustivel) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("combustivel_dropdown"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Orange,
                            focusedLabelColor = Orange
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCombustivel,
                        onDismissRequest = { expandedCombustivel = false }
                    ) {
                        tiposCombustivelMap.forEach { (key, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    combustivel = key
                                    expandedCombustivel = false
                                },
                                modifier = Modifier.testTag("combustivel_item_$key")
                            )
                        }
                    }
                }

                // --- acessórios com testTag ---
                Text(
                    stringResource(R.string.accessories),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF212121)
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    acessoriosMap.forEach { (key, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    if (acessoriosSelecionados.contains(key))
                                        acessoriosSelecionados.remove(key)
                                    else
                                        acessoriosSelecionados.add(key)
                                }
                                .padding(8.dp)
                                .testTag("accessory_$key"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, fontSize = 15.sp)
                            Checkbox(
                                checked = acessoriosSelecionados.contains(key),
                                onCheckedChange = null,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Orange
                                )
                            )
                        }
                    }
                }

                Divider(color = Color(0xFFE0E0E0))

                // --- status de negociação ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("negociacao_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { emNegociacao = !emNegociacao },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                stringResource(R.string.negotiation_status),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF212121)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                stringResource(R.string.negotiation_status_desc),
                                fontSize = 13.sp,
                                color = Color(0xFF757575)
                            )
                        }
                        Switch(
                            checked = emNegociacao,
                            onCheckedChange = { emNegociacao = it },
                            modifier = Modifier.testTag("negociacao_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFFD32F2F),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFBDBDBD)
                            )
                        )
                    }
                }

                // --- seleção de imagens ---
                OutlinedButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("select_images_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Orange
                    )
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.select_images))
                }

                // --- preview das imagens ---
                if (selectedImages.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("selected_images_preview")
                    ) {
                        items(selectedImages) { imgBytes ->
                            val bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.size)
                            bmp?.let {
                                Card(
                                    modifier = Modifier.size(100.dp),
                                    shape = RoundedCornerShape(12.dp)
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

                Divider(color = Color(0xFFE0E0E0))

                // --- botões ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("cancel_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            stringResource(R.string.cancel),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
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
                                carId = posterToEdit?.carId ?: 0,
                                emNegociacao = emNegociacao
                            )
                            onSave(car, poster, selectedImages)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("save_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Orange),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            stringResource(R.string.save),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
