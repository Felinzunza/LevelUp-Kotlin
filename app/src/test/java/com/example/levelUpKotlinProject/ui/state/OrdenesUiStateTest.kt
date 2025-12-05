package com.example.levelUpKotlinProject.ui.state

import org.junit.Assert.*
import org.junit.Test

class OrdenesUiStateTest {

    @Test
    fun `OrdenesUiState - estado inicial correcto`() {
        val state = OrdenesUiState()

        assertTrue(state.estaCargando)
        assertTrue(state.ordenes.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `OrdenesUiState - carga de error funciona`() {
        val state = OrdenesUiState(
            estaCargando = false,
            error = "Fallo de internet"
        )

        assertEquals("Fallo de internet", state.error)
        assertFalse(state.estaCargando)
    }
}