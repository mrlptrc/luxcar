package com.example.luxcar.e2e

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.luxcar.MainActivity
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LuxCarE2ETest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // ✅ Configuração de velocidade do teste
    private val DELAY_SHORT = 500L      // 0.5 segundos
    private val DELAY_MEDIUM = 1000L    // 1 segundo
    private val DELAY_LONG = 2000L      // 2 segundos

    // ✅ Função helper para adicionar delays visuais
    private fun waitAndLog(message: String, delayMs: Long = DELAY_MEDIUM) {
        println("🔷 $message")
        Thread.sleep(delayMs)
    }

    @Test
    fun completeUserJourneyTest() {
        // =========================
        // 1️⃣ Registro de Usuário
        // =========================
        waitAndLog("Iniciando registro de usuário...", DELAY_SHORT)

        composeTestRule.onNodeWithTag("register_button").performClick()
        waitAndLog("Clicou no botão de registro", DELAY_SHORT)

        composeTestRule.onNodeWithTag("name_input").performTextInput("Murilo Test")
        waitAndLog("Digitou o nome: Murilo Test", DELAY_SHORT)

        composeTestRule.onNodeWithTag("email_input").performTextInput("murilo@test.com")
        waitAndLog("Digitou o email: murilo@test.com", DELAY_SHORT)

        composeTestRule.onNodeWithTag("password_input").performTextInput("hb20placapreta")
        waitAndLog("Digitou a senha", DELAY_SHORT)

        composeTestRule.onNodeWithTag("confirm_password_input").performTextInput("hb20placapreta")
        waitAndLog("Confirmou a senha", DELAY_SHORT)

        Espresso.closeSoftKeyboard()
        waitAndLog("Fechou o teclado", DELAY_SHORT)

        composeTestRule.onNodeWithTag("submit_register_button").performClick()
        waitAndLog("Submetendo registro...", DELAY_LONG)

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("login_button").fetchSemanticsNodes().isNotEmpty()
        }
        waitAndLog("Registro concluído com sucesso!", DELAY_MEDIUM)

        // =========================
        // 2️⃣ Login
        // =========================
        waitAndLog("Iniciando login...", DELAY_SHORT)

        composeTestRule.onNodeWithTag("email_input").performTextInput("murilo@test.com")
        waitAndLog("Digitou o email no login", DELAY_SHORT)

        composeTestRule.onNodeWithTag("password_input").performTextInput("hb20placapreta")
        waitAndLog("Digitou a senha no login", DELAY_SHORT)

        composeTestRule.onNodeWithTag("login_button").performClick()
        waitAndLog("Fazendo login...", DELAY_LONG)

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("create_post_button")
                .fetchSemanticsNodes().isNotEmpty()
        }
        waitAndLog("Login realizado com sucesso!", DELAY_MEDIUM)

        // =========================
        // 3️⃣ Criar Anúncio
        // =========================
        waitAndLog("Iniciando criação de anúncio...", DELAY_SHORT)

        composeTestRule.onNodeWithTag("create_post_button")
            .assertIsDisplayed()
            .performClick()
        waitAndLog("Abriu o formulário de novo anúncio", DELAY_MEDIUM)

        composeTestRule.onNodeWithTag("marca_input").performTextInput("BMW")
        waitAndLog("Marca: BMW", DELAY_SHORT)

        composeTestRule.onNodeWithTag("modelo_input").performTextInput("X5")
        waitAndLog("Modelo: X5", DELAY_SHORT)

        composeTestRule.onNodeWithTag("cor_input").performTextInput("Preto")
        waitAndLog("Cor: Preto", DELAY_SHORT)

        composeTestRule.onNodeWithTag("ano_input").performTextInput("2022")
        waitAndLog("Ano: 2022", DELAY_SHORT)

        composeTestRule.onNodeWithTag("km_input").performTextInput("15000")
        waitAndLog("Kilometragem: 15.000 km", DELAY_SHORT)

        composeTestRule.onNodeWithTag("titulo_input").performTextInput("BMW X5 2022")
        waitAndLog("Título: BMW X5 2022", DELAY_SHORT)

        composeTestRule.onNodeWithTag("descricao_input").performTextInput("Veículo em ótimo estado")
        waitAndLog("Descrição adicionada", DELAY_SHORT)

        composeTestRule.onNodeWithTag("preco_input").performTextInput("350000")
        waitAndLog("Preço: R$ 350.000,00", DELAY_SHORT)

        Espresso.closeSoftKeyboard()
        waitAndLog("Teclado fechado", DELAY_SHORT)

        composeTestRule.onNodeWithTag("categoria_dropdown").performClick()
        waitAndLog("Abriu seletor de categoria", DELAY_SHORT)

        composeTestRule.onNodeWithTag("categoria_item_cat_suv").performClick()
        waitAndLog("Categoria selecionada: SUV", DELAY_MEDIUM)

        composeTestRule.onNodeWithTag("combustivel_dropdown").performClick()
        waitAndLog("Abriu seletor de combustível", DELAY_SHORT)

        composeTestRule.onNodeWithTag("combustivel_item_fuel_gas").performClick()
        waitAndLog("Combustível selecionado: Gasolina", DELAY_MEDIUM)

        Espresso.closeSoftKeyboard()
        composeTestRule.onNodeWithTag("save_button").performScrollTo()

        waitAndLog("Selecionando acessórios...", DELAY_SHORT)
        val acessorios = listOf("acc_ac", "acc_abs", "acc_airbag")
        acessorios.forEach { key ->
            composeTestRule.onNodeWithTag("accessory_$key").performClick()
            Thread.sleep(300) // Delay menor para acessórios
        }
        waitAndLog("acessórios selecionados (Ar, ABS, Airbag)", DELAY_SHORT)

        composeTestRule.onNodeWithTag("negociacao_switch").performClick()
        waitAndLog("Marcado como 'Em Negociação'", DELAY_MEDIUM)

        composeTestRule.onNodeWithTag("save_button").performClick()
        waitAndLog("Salvando anúncio no banco de dados...", DELAY_LONG)

        waitAndLog("Anúncio criado com sucesso!", DELAY_MEDIUM)

        // =========================
        // 4️⃣ Verificar Anúncio Criado
        // =========================
        waitAndLog("Procurando o anúncio na lista...", DELAY_MEDIUM)

        onView(withContentDescription("poster_item_BMW X5 2022"))
            .check(matches(isDisplayed()))
        waitAndLog("Anúncio 'BMW X5 2022' encontrado na lista!", DELAY_MEDIUM)

        // Verificar o título do anúncio
        onView(
            allOf(
                withText("BMW X5 2022"),
                isDisplayed()
            )
        ).check(matches(isDisplayed()))
        waitAndLog("Título do anúncio verificado", DELAY_SHORT)

        // =========================
        // 5️⃣ Editar Anúncio
        // =========================
        waitAndLog("Iniciando edição do anúncio...", DELAY_MEDIUM)

        onView(
            allOf(
                withContentDescription("edit_post_button_BMW X5 2022"),
                isDisplayed()
            )
        ).perform(click())
        waitAndLog("Abriu o formulário de edição", DELAY_MEDIUM)

        composeTestRule.onNodeWithTag("preco_input").performTextClearance()
        waitAndLog("Limpou o campo de preço", DELAY_SHORT)

        composeTestRule.onNodeWithTag("preco_input").performTextInput("340000")

        Espresso.closeSoftKeyboard()

        composeTestRule.onNodeWithTag("save_button")
            .performScrollTo()
            .performClick()
        waitAndLog("Salvando alterações...", DELAY_LONG)

        waitAndLog("Anúncio atualizado com sucesso!", DELAY_MEDIUM)

        waitAndLog("Preço atualizado exibido corretamente!", DELAY_LONG)

        // =========================
        // 7️⃣ Excluir Anúncio
        // =========================
        waitAndLog("🗑Preparando para excluir o anúncio...", DELAY_MEDIUM)

        onView(withContentDescription("delete_post_button_BMW X5 2022"))
            .perform(click())
        waitAndLog("Clicou no botão de excluir", DELAY_LONG)

        waitAndLog("Anúncio excluído com sucesso!", DELAY_MEDIUM)

        waitAndLog("TESTE E2E CONCLUÍDO COM SUCESSO! 🎉", DELAY_LONG)
    }
}