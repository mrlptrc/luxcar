package com.example.luxcar.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    logoResId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sobre o LuxCar") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_media_previous),
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFF1A1A1A),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.White,
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Image(
                    painter = painterResource(id = logoResId),
                    contentDescription = "Logo LuxCar",
                    modifier = Modifier
                        .height(100.dp)
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = "Bem-vindo ao LuxCar",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "O LUXCAR é a ferramenta definitiva para concessionárias e vendedores, " +
                            "desenhada para transformar a apresentação de veículos de luxo e seminovos premium. " +
                            "Permitimos que sua equipe ofereça uma experiência de atendimento rápida, moderna e tecnológica, " +
                            "diretamente no conforto e segurança do showroom.\n\n" +

                            "Com o LUXCAR, você garante:\n\n" +
                            "• Apresentação Dinâmica: Vendedores podem mostrar o catálogo de veículos de forma mais rápida e tecnológica, " +
                            "substituindo o antigo anúncio por uma consulta interativa em tablet.\n\n" +
                            "• Visualização Instantânea: Clientes podem visualizar todos os carros disponíveis com a chegada na concessionária e em tempo real.\n\n" +
                            "• Consulta Detalhada: Acesso imediato a detalhes, fotos e preços de cada veículo, " +
                            "com confiabilidade nas informações de venda.\n\n" +
                            "• Experiência Premium: Oferece uma experiência moderna, segura e eficiente tanto para vendedores quanto para compradores.\n\n" +
                            "• Nossa Missão: Transformar o mercado automotivo digital, unindo tecnologia, segurança e experiência do usuário.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "message/rfc822"
                            putExtra(Intent.EXTRA_EMAIL, arrayOf("luxcar_support@googlegroups.com"))
                            putExtra(Intent.EXTRA_SUBJECT, "Suporte Técnico/Comercial - Aplicativo LUXCAR")
                            putExtra(
                                Intent.EXTRA_TEXT,
                                """
                                Prezada Equipe LUXCAR,
                                
                                Detalhe sua solicitação abaixo. Caso seja um problema técnico, por favor, inclua o máximo de informações possível:
                                
                                ---------------------------------------------------
                                Tipo de Solicitação: (Ex: Dúvida, Erro, Sugestão, Contato Comercial)
                                Nome da Concessionária: 
                                Nome do Contato: 
                                Telefone/WhatsApp: 
                                
                                Descrição Detalhada:
                                [Insira aqui a descrição detalhada da sua solicitação]
                                
                                ---------------------------------------------------
                                Agradecemos o seu contato.
                                """.trimIndent()
                            )
                        }

                        try {
                            context.startActivity(
                                Intent.createChooser(intent, "Escolha um aplicativo de e-mail:")
                            )
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(
                                context,
                                "Nenhum aplicativo de e-mail encontrado.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A))
                ) {
                    Text(
                        text = "Entrar em contato com o Suporte",
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    )
}
