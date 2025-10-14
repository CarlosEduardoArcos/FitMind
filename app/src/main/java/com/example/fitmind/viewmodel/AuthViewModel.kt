package com.example.fitmind.viewmodel

import android.util.Log
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
            if (success && auth?.currentUser != null) {
                val uid = auth!!.currentUser!!.uid
                
                // Inicializar datos del usuario (verificar campo hábitos)
                repo.initializeUserData(uid) { initSuccess, initMsg ->
                    _isLoading.value = false
                    
                    if (initSuccess) {
                        loadUserRole(uid)
                        onResult(true, null)
                    } else {
                        // Continuar con el login aunque falle la inicialización
                        loadUserRole(uid)
                        onResult(true, "Login exitoso pero ${initMsg ?: "error en inicialización"}")
                    }
                }
            } else {
                _isLoading.value = false
                onResult(success, msg)
            }
        }
    }

    fun register(nombre: String, email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        if (auth == null) {
            onResult(false, "Firebase no está inicializado")
            return
        }
        
        _isLoading.value = true
        repo.registerUser(nombre, email, password) { success, msg ->
            if (success && auth?.currentUser != null) {
                val uid = auth!!.currentUser!!.uid
                
                // Verificar que los datos se crearon correctamente
                repo.initializeUserData(uid) { initSuccess, initMsg ->
                    _isLoading.value = false
                    
                    if (initSuccess) {
                        loadUserRole(uid)
                        onResult(true, null)
                    } else {
                        // Continuar con el registro aunque falle la verificación
                        loadUserRole(uid)
                        onResult(true, "Registro exitoso pero ${initMsg ?: "error en verificación"}")
                    }
                }
            } else {
                _isLoading.value = false
                onResult(success, msg)
            }
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
                // Si el usuario es carloeduardo1987@gmail.com, asegurar que tenga rol admin
                val currentUserEmail = auth?.currentUser?.email
                val finalRole = if (currentUserEmail == "carloeduardo1987@gmail.com") {
                    "admin"
                } else {
                    role ?: "usuario"
                }
                
                _userRole.value = finalRole
                
                // Si es admin pero no está en Firestore, actualizar Firestore
                if (finalRole == "admin" && role != "admin" && currentUserEmail == "carloeduardo1987@gmail.com") {
                    repo.updateUserRole(uid, "admin")
                }
            }
        }
    }

    fun isAdmin(): Boolean {
        return _userRole.value == "admin"
    }

    fun getCurrentUserId(): String? {
        return _currentUser.value?.uid
    }

    fun isUser(): Boolean {
        return _userRole.value == "usuario"
    }
    
    /**
     * Ejecuta la migración de usuarios existentes para asegurar que tengan el campo hábitos
     * Esta función debe llamarse una sola vez al inicializar la app
     */
    fun migrateExistingUsersIfNeeded() {
        viewModelScope.launch {
            try {
                repo.validateFirestoreStructure { isValid, message ->
                    if (!isValid) {
                        Log.d("AuthViewModel", "Estructura de Firestore necesita migración: $message")
                        
                        // Ejecutar migración
                        repo.migrateAllUsersToHaveHabitsField(
                            onProgress = { processed, total ->
                                Log.d("AuthViewModel", "Migración progreso: $processed/$total")
                            },
                            onComplete = { success, resultMessage ->
                                Log.d("AuthViewModel", "Migración completada: $success - $resultMessage")
                            }
                        )
                    } else {
                        Log.d("AuthViewModel", "Estructura de Firestore válida: $message")
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error durante validación/migración", e)
            }
        }
    }
}