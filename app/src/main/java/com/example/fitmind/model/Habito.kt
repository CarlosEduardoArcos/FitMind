package com.example.fitmind.model

import java.util.UUID

data class Habito(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val categoria: String,
    val frecuencia: String,
    val completado: Boolean = false,
    val usuarioId: String = ""
)
