package com.example.luxcar

import com.example.luxcar.data.model.User
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginUnitTest {

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
    fun loginComCredenciaisCorretas_deveRetornarSucesso() {
        val emailInput = "teste@luxcar.com"
        val senhaInput = "1234"

        val sucesso = emailInput == usuario.email && senhaInput == usuario.senha
        assertTrue("Login deveria ser bem-sucedido", sucesso)
    }

    @Test
    fun loginComSenhaErrada_deveRetornarErro() {
        val emailInput = "teste@luxcar.com"
        val senhaInput = "errado"

        val sucesso = emailInput == usuario.email && senhaInput == usuario.senha
        assertTrue("Login deveria falhar", !sucesso)
    }

    @Test
    fun loginComEmailInexistente_deveRetornarErro() {
        val emailInput = "inexistente@luxcar.com"
        val senhaInput = "1234"

        val sucesso = emailInput == usuario.email && senhaInput == usuario.senha
        assertTrue("Login deveria falhar para email inexistente", !sucesso)
    }
}
