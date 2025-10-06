package com.example.fitmind.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Representa un hábito que el usuario desea registrar.
 * Compatible con Firebase (constructor vacío y propiedades por defecto).
 */
@IgnoreExtraProperties
data class Habito(
    var id: String = "",
    var nombre: String = "",
    var categoria: String = "",
    var frecuencia: String = "",
    var fechaInicio: String = "",
    var usuarioId: String = ""
) {
    // Constructor vacío requerido por Firebase
    constructor() : this("", "", "", "", "", "")
}


