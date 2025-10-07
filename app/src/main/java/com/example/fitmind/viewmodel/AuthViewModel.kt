package com.example.fitmind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitmind.core.AppConfig
import com.example.fitmind.data.mock.MockAuthRepository
import com.example.fitmind.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for user authentication state and actions.
 * Supports both Firebase and Mock modes.
 */
class AuthViewModel(
    private val firebaseRepository: FirebaseRepository = FirebaseRepository(),
    private val mockRepository: MockAuthRepository = MockAuthRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun register(email: String, password: String, nombre: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = if (AppConfig.isMockMode) {
                    mockRepository.registerUser(email, password, nombre)
                } else {
                    firebaseRepository.registerUser(email, password, nombre)
                }
                
                if (result.isSuccess) {
                    _isAuthenticated.value = true
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Error en el registro"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                if (AppConfig.isMockMode) {
                    // Simular delay para mejor UX en modo mock
                    kotlinx.coroutines.delay(1000)
                    // En modo mock, siempre simular login exitoso
                    _isAuthenticated.value = true
                    _errorMessage.value = null
                } else {
                    val result = firebaseRepository.loginUser(email, password)
                    if (result.isSuccess) {
                        _isAuthenticated.value = true
                        _errorMessage.value = null
                    } else {
                        _errorMessage.value = result.exceptionOrNull()?.message ?: "Error en el login"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        if (AppConfig.isMockMode) {
            mockRepository.logoutUser()
        } else {
            firebaseRepository.logoutUser()
        }
        _isAuthenticated.value = false
    }

    fun getCurrentUserId(): String? = if (_isAuthenticated.value) {
        if (AppConfig.isGuestMode) {
            AppConfig.Mock.defaultUserId
        } else if (AppConfig.isMockMode) {
            mockRepository.getCurrentUserId()
        } else {
            firebaseRepository.getCurrentUserId()
        }
    } else null

    fun checkUserSession() {
        if (AppConfig.isGuestMode) {
            _isAuthenticated.value = true
            return
        }
        
        val currentUser = if (AppConfig.isMockMode) {
            mockRepository.getCurrentUserId()
        } else {
            firebaseRepository.getCurrentUserId()
        }
        _isAuthenticated.value = currentUser != null
    }
}


