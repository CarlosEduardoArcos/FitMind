package com.example.fitmind.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitmind.utils.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar la configuración de la aplicación
 */
class SettingsViewModel : ViewModel() {
    
    private val _isOnlineModeEnabled = MutableStateFlow(true)
    val isOnlineModeEnabled: StateFlow<Boolean> = _isOnlineModeEnabled.asStateFlow()
    
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    
    private val _connectionType = MutableStateFlow("Sin conexión")
    val connectionType: StateFlow<String> = _connectionType.asStateFlow()
    
    // Estado combinado para mostrar el estado actual de la app
    private val _appModeStatus = MutableStateFlow(AppModeStatus.ONLINE_CONNECTED)
    val appModeStatus: StateFlow<AppModeStatus> = _appModeStatus.asStateFlow()
    
    // Estado para el tema oscuro
    private val _darkTheme = MutableStateFlow(false)
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()
    
    // DataStore para persistir la preferencia del usuario
    private lateinit var dataStore: DataStore<Preferences>
    
    companion object {
        private val ONLINE_MODE_KEY = booleanPreferencesKey("online_mode_enabled")
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme_enabled")
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    }
    
    /**
     * Inicializa el ViewModel con el contexto
     */
    fun initializeContext(context: Context) {
        dataStore = context.dataStore
        
        // Cargar preferencias guardadas
        loadSavedPreferences()
        
        // Observar cambios en la conexión
        observeNetworkConnection(context)
        
        // Combinar estados para determinar el estado de la app
        combine(
            isOnlineModeEnabled,
            isConnected
        ) { onlineMode, connected ->
            when {
                onlineMode && connected -> AppModeStatus.ONLINE_CONNECTED
                onlineMode && !connected -> AppModeStatus.ONLINE_NO_CONNECTION
                !onlineMode -> AppModeStatus.OFFLINE_MODE
                else -> AppModeStatus.OFFLINE_MODE
            }
        }.distinctUntilChanged()
            .let { flow ->
                viewModelScope.launch {
                    flow.collect { status ->
                        _appModeStatus.value = status
                    }
                }
            }
    }
    
    /**
     * Carga las preferencias guardadas del usuario
     */
    private fun loadSavedPreferences() {
        viewModelScope.launch {
            try {
                val preferences = dataStore.data.first()
                val savedOnlineMode = preferences[ONLINE_MODE_KEY] ?: true
                val savedDarkTheme = preferences[DARK_THEME_KEY] ?: false
                _isOnlineModeEnabled.value = savedOnlineMode
                _darkTheme.value = savedDarkTheme
            } catch (e: Exception) {
                // Si hay error, usar valores por defecto
                _isOnlineModeEnabled.value = true
                _darkTheme.value = false
            }
        }
    }
    
    /**
     * Observa cambios en la conexión de red
     */
    private fun observeNetworkConnection(context: Context) {
        viewModelScope.launch {
            NetworkUtils.observeInternetConnection(context).collect { isConnected ->
                _isConnected.value = isConnected
                _connectionType.value = NetworkUtils.getConnectionType(context)
            }
        }
    }
    
    /**
     * Cambia el modo de la aplicación (online/offline)
     */
    fun setOnlineMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                _isOnlineModeEnabled.value = enabled
                
                // Guardar la preferencia
                dataStore.edit { preferences ->
                    preferences[ONLINE_MODE_KEY] = enabled
                }
            } catch (e: Exception) {
                // En caso de error, revertir el cambio
                _isOnlineModeEnabled.value = !enabled
            }
        }
    }
    
    /**
     * Cambia el tema de la aplicación (oscuro/claro)
     */
    fun toggleDarkTheme() {
        viewModelScope.launch {
            try {
                val newDarkTheme = !_darkTheme.value
                _darkTheme.value = newDarkTheme
                
                // Guardar la preferencia
                dataStore.edit { preferences ->
                    preferences[DARK_THEME_KEY] = newDarkTheme
                }
            } catch (e: Exception) {
                // En caso de error, revertir el cambio
                _darkTheme.value = !_darkTheme.value
            }
        }
    }
    
    /**
     * Obtiene el mensaje de estado actual
     */
    fun getStatusMessage(): String {
        return when (_appModeStatus.value) {
            AppModeStatus.ONLINE_CONNECTED -> "🟢 Conectado (modo online)"
            AppModeStatus.ONLINE_NO_CONNECTION -> "🔴 Sin conexión (modo local temporal)"
            AppModeStatus.OFFLINE_MODE -> "🔴 Modo sin conexión"
        }
    }
    
    /**
     * Obtiene el mensaje para mostrar al usuario cuando cambia el modo
     */
    fun getModeChangeMessage(enabled: Boolean): String {
        return if (enabled) {
            "Modo en línea activado: los datos se sincronizarán con Firebase."
        } else {
            "Modo sin conexión activado: los datos se guardarán localmente."
        }
    }
    
    /**
     * Obtiene el mensaje para mostrar cuando se detecta cambio de conexión
     */
    fun getConnectionChangeMessage(): String {
        return if (_isConnected.value) {
            if (_isOnlineModeEnabled.value) {
                "Conexión restaurada: modo online activo."
            } else {
                "Conexión disponible: modo offline activo."
            }
        } else {
            if (_isOnlineModeEnabled.value) {
                "Sin conexión: operando temporalmente en modo local."
            } else {
                "Sin conexión: modo offline activo."
            }
        }
    }
    
    /**
     * Verifica si se debe usar Firebase para guardar datos
     */
    fun shouldUseFirebase(): Boolean {
        return _isOnlineModeEnabled.value && _isConnected.value
    }
    
    /**
     * Verifica si se debe sincronizar con Firebase cuando se recupera la conexión
     */
    fun shouldSyncOnConnection(): Boolean {
        return _isOnlineModeEnabled.value && _isConnected.value
    }
}

/**
 * Estados posibles del modo de la aplicación
 */
enum class AppModeStatus {
    ONLINE_CONNECTED,      // Modo online con conexión
    ONLINE_NO_CONNECTION,  // Modo online sin conexión (temporalmente local)
    OFFLINE_MODE          // Modo offline activo
}
