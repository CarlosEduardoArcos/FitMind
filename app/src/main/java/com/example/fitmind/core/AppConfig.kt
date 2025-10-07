package com.example.fitmind.core

/**
 * Configuración global de la aplicación.
 * Cambia isMockMode a false cuando quieras usar Firebase real.
 */
object AppConfig {
    var isMockMode = true
    var isGuestMode = false
    
    // Configuraciones adicionales para el modo mock
    object Mock {
        const val defaultUserId = "mock_user_123"
        const val defaultUserName = "Usuario Demo"
        const val defaultUserEmail = "demo@fitmind.com"
    }
}
