package com.example.fitmind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitmind.data.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repo = FirebaseRepository()
    
    // OPT: Inicialización segura de FirebaseAuth
    private val auth by lazy { 
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    private val _currentUser = MutableStateFlow(auth?.currentUser)
    val currentUser: StateFlow<com.google.firebase.auth.FirebaseUser?> = _currentUser.asStateFlow()

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // OPT: Escuchar cambios en el estado de autenticación de forma segura
        auth?.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
            if (firebaseAuth.currentUser != null) {
                loadUserRole(firebaseAuth.currentUser!!.uid)
            } else {
                _userRole.value = null
            }
        }
    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        if (auth == null) {
            onResult(false, "Firebase no está inicializado")
            return
        }
        
        _isLoading.value = true
        repo.loginUser(email, password) { success, msg ->
            _isLoading.value = false
            if (success && auth?.currentUser != null) {
                loadUserRole(auth!!.currentUser!!.uid)
            }
            onResult(success, msg)
        }
    }

    fun register(nombre: String, email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        if (auth == null) {
            onResult(false, "Firebase no está inicializado")
            return
        }
        
        _isLoading.value = true
        repo.registerUser(nombre, email, password) { success, msg ->
            _isLoading.value = false
            if (success && auth?.currentUser != null) {
                loadUserRole(auth!!.currentUser!!.uid)
            }
            onResult(success, msg)
        }
    }

    fun logout() {
        auth?.signOut()
        _currentUser.value = null
        _userRole.value = null
    }

    private fun loadUserRole(uid: String) {
        viewModelScope.launch {
            repo.getUserRole(uid) { role ->
                _userRole.value = role
            }
        }
    }

    fun isAdmin(): Boolean {
        return _userRole.value == "admin"
    }

    fun isUser(): Boolean {
        return _userRole.value == "usuario"
    }

    fun getCurrentUserId(): String? {
        return _currentUser.value?.uid
    }
}