package com.example.fitmind.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Representa el seguimiento diario de un hábito.
 * Compatible con Firebase (constructor vacío y propiedades por defecto).
 */
@IgnoreExtraProperties
data class Registro(
    var id: String = "",
    var habitoId: String = "",
    var fecha: String = "",
    var completado: Boolean = false
) {
    // Constructor vacío requerido por Firebase
    constructor() : this("", "", "", false)
}


