package com.example.fitmind.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Representa una notificación o recordatorio configurado por el usuario.
 * Compatible con Firebase (constructor vacío y propiedades por defecto).
 */
@IgnoreExtraProperties
data class Notificacion(
    var id: String = "",
    var mensaje: String = "",
    var fechaHora: String = "",
    var usuarioId: String = ""
) {
    // Constructor vacío requerido por Firebase
    constructor() : this("", "", "", "")
}


