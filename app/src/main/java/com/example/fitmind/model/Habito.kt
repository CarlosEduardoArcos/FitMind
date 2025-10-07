package com.example.fitmind.model

data class Habito(
    val id: String = "",
    val nombre: String,
    val categoria: String,
    val frecuencia: String,
    val completado: Boolean = false
)
