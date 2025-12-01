package com.example.luxcar.e2e

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.luxcar.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LuxCarE2ETest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun completeUserJourneyTest() {
        // =========================
        // 1️⃣ Registro de Usuário
        // =========================
        composeTestRule.onNodeWithTag("register_button").performClick()
        composeTestRule.onNodeWithTag("name_input").performTextInput("Murilo Test")
        composeTestRule.onNodeWithTag("email_input").performTextInput("murilo@test.com")
        composeTestRule.onNodeWithTag("password_input").performTextInput("hb20placapreta")
        composeTestRule.onNodeWithTag("confirm_password_input").performTextInput("hb20placapreta")
        Espresso.closeSoftKeyboard()
        composeTestRule.onNodeWithTag("submit_register_button").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) { //
            composeTestRule.onAllNodesWithTag("login_button").fetchSemanticsNodes().isNotEmpty()
        }

        // =========================
        // 2️⃣ Login
        // =========================
        composeTestRule.onNodeWithTag("email_input").performTextInput("murilo@test.com")
        composeTestRule.onNodeWithTag("password_input").performTextInput("hb20placapreta")
        composeTestRule.onNodeWithTag("login_button").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) { //
            composeTestRule.onAllNodesWithTag("create_post_button")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // =========================
        // 3️⃣ Criar Anúncio
        // =========================
        composeTestRule.onNodeWithTag("create_post_button")
            .assertIsDisplayed() //
            .performClick()

        composeTestRule.onNodeWithTag("marca_input").performTextInput("BMW")
        composeTestRule.onNodeWithTag("modelo_input").performTextInput("X5")
        composeTestRule.onNodeWithTag("cor_input").performTextInput("Preto")
        composeTestRule.onNodeWithTag("ano_input").performTextInput("2022")
        composeTestRule.onNodeWithTag("km_input").performTextInput("15000")
        composeTestRule.onNodeWithTag("titulo_input").performTextInput("BMW X5 2022")
        composeTestRule.onNodeWithTag("descricao_input").performTextInput("Veículo em ótimo estado")
        composeTestRule.onNodeWithTag("preco_input").performTextInput("350000")
        Espresso.closeSoftKeyboard()

        composeTestRule.onNodeWithTag("categoria_dropdown").performClick()
        composeTestRule.onNodeWithTag("categoria_item_cat_suv").performClick()

        composeTestRule.onNodeWithTag("combustivel_dropdown").performClick()
        composeTestRule.onNodeWithTag("combustivel_item_fuel_gas").performClick()

        Espresso.closeSoftKeyboard()
        composeTestRule.onNodeWithTag("save_button").performScrollTo()

        val acessorios = listOf("acc_ac", "acc_abs", "acc_airbag")
        acessorios.forEach { key ->
            composeTestRule.onNodeWithTag("accessory_$key").performClick()
        }

        composeTestRule.onNodeWithTag("negociacao_switch").performClick()

        composeTestRule.onNodeWithTag("save_button").performClick()

        Thread.sleep(1000)
/*
        // =========================
        // 4️⃣ Editar o último anúncio
        // =========================
        composeTestRule.waitUntil(timeoutMillis = 7000) {
            composeTestRule.onAllNodesWithTag("edit_post_button").fetchSemanticsNodes().isNotEmpty()
        }

        // Pega o último botão de editar disponível e clica
        composeTestRule.onAllNodesWithTag("edit_post_button").onLast().performScrollTo().performClick()

        composeTestRule.onNodeWithTag("preco_input")
            .performTextClearance()
            .performTextInput("340000")
        Espresso.closeSoftKeyboard()

        composeTestRule.onNodeWithTag("save_button")
            .performScrollTo()
            .performClick()

        // =========================
        // 5️⃣ Consultar/Listar Anúncio
        // =========================
        composeTestRule.onNodeWithTag("poster_item_BMW X5 2022")
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("poster_price_BMW X5 2022")
            .assertTextEquals("340000")

        // =========================
        // 6️⃣ Excluir Anúncio
        // =========================
        composeTestRule.onNodeWithTag("poster_item_BMW X5 2022")
            .performScrollTo()
            .performClick()
        composeTestRule.onNodeWithTag("delete_post_button")
            .performScrollTo()
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("poster_item_BMW X5 2022")
                .fetchSemanticsNodes().isEmpty()
        }

 */
    }
}

private fun Unit.performTextInput(text: String) {}
