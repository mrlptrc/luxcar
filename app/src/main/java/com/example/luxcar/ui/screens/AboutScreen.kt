package com.example.luxcar.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luxcar.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    logoResId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val Orange = Color(0xFFFF9800)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.about_title),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_media_previous),
                            contentDescription = stringResource(R.string.back)
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // logo
            Image(
                painter = painterResource(id = logoResId),
                contentDescription = stringResource(R.string.logo_description),
                modifier = Modifier
                    .height(100.dp)
                    .padding(bottom = 24.dp)
            )

            // título
            Text(
                text = stringResource(R.string.welcome_luxcar),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // descrição principal
            Text(
                text = stringResource(R.string.about_description),
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = Color(0xFF424242),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(24.dp))

            // benefícios
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.benefits_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF212121)
                )

                BenefitItem(
                    title = stringResource(R.string.benefit_1_title),
                    description = stringResource(R.string.benefit_1_desc)
                )

                BenefitItem(
                    title = stringResource(R.string.benefit_2_title),
                    description = stringResource(R.string.benefit_2_desc)
                )

                BenefitItem(
                    title = stringResource(R.string.benefit_3_title),
                    description = stringResource(R.string.benefit_3_desc)
                )

                BenefitItem(
                    title = stringResource(R.string.benefit_4_title),
                    description = stringResource(R.string.benefit_4_desc)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // missão
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.mission_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Orange
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.mission_description),
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = Color(0xFF424242)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // botão contato
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "message/rfc822"
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("luxcar_support@googlegroups.com"))
                        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject))
                        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_template))
                    }

                    try {
                        context.startActivity(
                            Intent.createChooser(intent, context.getString(R.string.email_chooser))
                        )
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.email_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.contact_support),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun BenefitItem(title: String, description: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .padding(top = 8.dp)
                    .background(
                        Color(0xFFFF9800),
                        androidx.compose.foundation.shape.CircleShape
                    )
            )
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF212121)
                )
                Text(
                    text = description,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFF616161)
                )
            }
        }
    }
}