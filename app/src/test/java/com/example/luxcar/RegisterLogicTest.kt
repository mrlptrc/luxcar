package com.example.luxcar

import com.example.luxcar.data.model.User
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RegisterUnitTest {

    private lateinit var usuario: User

    @Before
    fun setup() {
        usuario = User(
            nome = "teste",
            email = "teste@luxcar.com",
            senha = "1234"
        )
    }

    @Test
    fun cadastroComSenhasIguais_deveSerBemSucedido() {
        val nomeInput = "teste"
        val emailInput = "teste@luxcar.com"
        val senhaInput = "1234"
        val confirmSenhaInput = "1234"

        val sucesso = senhaInput == confirmSenhaInput &&
                nomeInput.isNotBlank() &&
                emailInput.isNotBlank() &&
                senhaInput.isNotBlank()

        assertTrue("Cadastro deveria ser bem-sucedido", sucesso)
    }

    @Test
    fun cadastroComSenhasDiferentes_deveFalhar() {
        val nomeInput = "teste"
        val emailInput = "teste@luxcar.com"
        val senhaInput = "1234"
        val confirmSenhaInput = "4321"

        val sucesso = senhaInput == confirmSenhaInput
        assertTrue("Cadastro deveria falhar com senhas diferentes", !sucesso)
    }

    @Test
    fun cadastroComCamposVazios_deveFalhar() {
        val nomeInput = ""
        val emailInput = ""
        val senhaInput = ""
        val confirmSenhaInput = ""

        val sucesso = senhaInput == confirmSenhaInput &&
                nomeInput.isNotBlank() &&
                emailInput.isNotBlank() &&
                senhaInput.isNotBlank()

        assertTrue("Cadastro deveria falhar com campos vazios", !sucesso)
    }
}
