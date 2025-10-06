package com.example.fitmind.data.mock

import com.example.fitmind.core.AppConfig

/**
 * Repositorio mock para autenticación.
 * Simula el comportamiento de Firebase Auth sin conexión real.
 */
class MockAuthRepository {
    private var loggedInUser: String? = null
    private var currentUserEmail: String? = null
    private var currentUserName: String? = null

    suspend fun registerUser(email: String, password: String, name: String): Result<Boolean> {
        return try {
            // Simular delay de red
            kotlinx.coroutines.delay(1000)
            
            loggedInUser = AppConfig.Mock.defaultUserId
            currentUserEmail = email
            currentUserName = name
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<Boolean> {
        return try {
            // Simular delay de red
            kotlinx.coroutines.delay(1000)
            
            loggedInUser = AppConfig.Mock.defaultUserId
            currentUserEmail = email
            currentUserName = AppConfig.Mock.defaultUserName
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logoutUser() {
        loggedInUser = null
        currentUserEmail = null
        currentUserName = null
    }

    fun getCurrentUserId(): String? = loggedInUser
    
    fun getCurrentUserEmail(): String? = currentUserEmail
    
    fun getCurrentUserName(): String? = currentUserName
    
    fun isUserLoggedIn(): Boolean = loggedInUser != null
}
