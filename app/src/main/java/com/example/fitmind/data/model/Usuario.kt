package com.example.fitmind.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Representa a un usuario registrado en la app FitMind.
 * Compatible con Firebase (constructor vacío y propiedades por defecto).
 */
@IgnoreExtraProperties
data class Usuario(
    var id: String = "",
    var nombre: String = "",
    var email: String = "",
    var fechaRegistro: String = ""
) {
    // Constructor vacío requerido por Firebase
    constructor() : this("", "", "", "")
}


