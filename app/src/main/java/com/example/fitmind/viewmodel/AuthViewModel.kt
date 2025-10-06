package com.example.fitmind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitmind.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for user authentication state and actions.
 */
class AuthViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isAuthenticated = MutableStateFlow(false) // Mock: starts as false
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun register(email: String, password: String, nombre: String) {
        _isLoading.value = true
        viewModelScope.launch {
            // Mock: simulate network delay
            kotlinx.coroutines.delay(1000)
            _isLoading.value = false
            
            // Mock: simple validation
            if (email.isNotBlank() && password.length >= 6 && nombre.isNotBlank()) {
                _isAuthenticated.value = true
                _errorMessage.value = null
            } else {
                _errorMessage.value = "Por favor completa todos los campos correctamente"
            }
        }
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            // Mock: simulate network delay
            kotlinx.coroutines.delay(1000)
            _isLoading.value = false
            
            // Mock: simple validation
            if (email.isNotBlank() && password.isNotBlank()) {
                _isAuthenticated.value = true
                _errorMessage.value = null
            } else {
                _errorMessage.value = "Email y contraseña son requeridos"
            }
        }
    }

    fun logout() {
        _isAuthenticated.value = false
    }

    fun getCurrentUserId(): String? = if (_isAuthenticated.value) "mock_user_id" else null

    fun checkUserSession() {
        val currentUser = repository.getCurrentUserId()
        _isAuthenticated.value = currentUser != null
    }
}


