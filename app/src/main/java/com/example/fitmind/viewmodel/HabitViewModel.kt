package com.example.fitmind.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitmind.data.local.*
import com.example.fitmind.data.FirebaseRepository
import com.example.fitmind.model.Habito
import com.example.fitmind.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HabitViewModel : ViewModel() {
    private var context: Context? = null
    private val _habits = MutableStateFlow<List<Habito>>(emptyList())
    val habits: StateFlow<List<Habito>> = _habits
    
    private val _settingsViewModel = MutableStateFlow<SettingsViewModel?>(null)
    private val _firebaseRepository = MutableStateFlow<FirebaseRepository?>(null)
    
    private val _pendingSyncHabits = MutableStateFlow<List<Habito>>(emptyList())
    val pendingSyncHabits: StateFlow<List<Habito>> = _pendingSyncHabits

    fun initializeContext(context: Context, settingsViewModel: SettingsViewModel? = null, firebaseRepository: FirebaseRepository? = null) {
        this.context = context
        _settingsViewModel.value = settingsViewModel
        _firebaseRepository.value = firebaseRepository
        loadHabits()
        
        // Observar cambios en la conexión para sincronización automática
        observeConnectionChanges()
    }
    
    private fun loadHabits() {
        val ctx = context ?: return
        viewModelScope.launch {
            try {
                getLocalHabitsFlow(ctx)
                    .map { set -> set.map { s -> deserializeHabito(s) } }
                    .collect { list -> _habits.value = list }
            } catch (e: Exception) {
                // Si hay error, inicializar con lista vacía
                _habits.value = emptyList()
            }
        }
    }

    fun addHabitLocal(hab: Habito) {
        val ctx = context ?: return
        val settingsVM = _settingsViewModel.value
        val firebaseRepo = _firebaseRepository.value
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Siempre guardar localmente primero
                saveHabitLocally(ctx, serializeHabito(hab))
                
                // Verificar si debe sincronizar con Firebase
                val shouldSync = shouldSyncWithFirebase()
                
                if (shouldSync && firebaseRepo != null) {
                    try {
                        // Obtener userId del AuthViewModel o usar un valor por defecto
                        val userId = "local_user" // TODO: Integrar con AuthViewModel
                        val habitMap = mapOf(
                            "id" to hab.id,
                            "nombre" to hab.nombre,
                            "categoria" to hab.categoria,
                            "frecuencia" to hab.frecuencia,
                            "completado" to hab.completado,
                            "usuarioId" to userId
                        )
                        firebaseRepo.addHabit(userId, habitMap) { success, error ->
                            if (success) {
                                showToast(ctx, "Modo online activo: hábito sincronizado con la nube.")
                            } else {
                                // Si falla la sincronización, agregar a la lista de pendientes
                                addToPendingSync(hab)
                                showToast(ctx, "Sin conexión: el hábito se guardará localmente.")
                            }
                        }
                    } catch (e: Exception) {
                        // Si falla la sincronización, agregar a la lista de pendientes
                        addToPendingSync(hab)
                        showToast(ctx, "Sin conexión: el hábito se guardará localmente.")
                    }
                } else {
                    showToast(ctx, "Sin conexión: el hábito se guardará localmente.")
                }
            } catch (e: Exception) {
                showToast(ctx, "Error al guardar el hábito.")
            }
        }
    }

    fun deleteHabitLocal(hab: Habito) {
        val ctx = context ?: return
        val firebaseRepo = _firebaseRepository.value
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Siempre eliminar localmente primero
                deleteHabitLocally(ctx, serializeHabito(hab))
                
                // Si está conectado y tiene Firebase, también eliminar de la nube
                if (shouldSyncWithFirebase() && firebaseRepo != null) {
                    try {
                        val userId = "local_user" // TODO: Integrar con AuthViewModel
                        val habitMap = mapOf(
                            "id" to hab.id,
                            "nombre" to hab.nombre,
                            "categoria" to hab.categoria,
                            "frecuencia" to hab.frecuencia,
                            "completado" to hab.completado,
                            "usuarioId" to userId
                        )
                        firebaseRepo.deleteHabit(userId, habitMap) { success ->
                            // Si falla la eliminación en Firebase, no es crítico
                            // El hábito ya fue eliminado localmente
                        }
                    } catch (e: Exception) {
                        // Si falla la eliminación en Firebase, no es crítico
                        // El hábito ya fue eliminado localmente
                    }
                }
            } catch (e: Exception) {
                showToast(ctx, "Error al eliminar el hábito.")
            }
        }
    }

    fun toggleComplete(hab: Habito) {
        val ctx = context ?: return
        val firebaseRepo = _firebaseRepository.value
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updated = hab.copy(completado = !hab.completado)
                deleteHabitLocally(ctx, serializeHabito(hab))
                saveHabitLocally(ctx, serializeHabito(updated))
                
                // Si está conectado y tiene Firebase, también actualizar en la nube
                if (shouldSyncWithFirebase() && firebaseRepo != null) {
                    try {
                        val userId = "local_user" // TODO: Integrar con AuthViewModel
                        val oldHabitMap = mapOf(
                            "id" to hab.id,
                            "nombre" to hab.nombre,
                            "categoria" to hab.categoria,
                            "frecuencia" to hab.frecuencia,
                            "completado" to hab.completado,
                            "usuarioId" to userId
                        )
                        val newHabitMap = mapOf(
                            "id" to updated.id,
                            "nombre" to updated.nombre,
                            "categoria" to updated.categoria,
                            "frecuencia" to updated.frecuencia,
                            "completado" to updated.completado,
                            "usuarioId" to userId
                        )
                        // Eliminar el hábito viejo y agregar el nuevo
                        firebaseRepo.deleteHabit(userId, oldHabitMap) { success ->
                            if (success) {
                                firebaseRepo.addHabit(userId, newHabitMap) { addSuccess, _ ->
                                    // Si falla la actualización en Firebase, no es crítico
                                    // El hábito ya fue actualizado localmente
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // Si falla la actualización en Firebase, no es crítico
                        // El hábito ya fue actualizado localmente
                    }
                }
            } catch (e: Exception) {
                showToast(ctx, "Error al actualizar el hábito.")
            }
        }
    }

    private fun serializeHabito(h: Habito): String {
        return try {
            "${h.id}|${h.nombre}|${h.categoria}|${h.frecuencia}|${h.completado}"
        } catch (e: Exception) {
            // Si hay error en serialización, retornar string vacío
            ""
        }
    }

    private fun deserializeHabito(s: String): Habito {
        return try {
            val parts = s.split("|")
            if (parts.size >= 5) {
                // Si el ID está vacío, generar uno nuevo
                val id = if (parts[0].isBlank()) java.util.UUID.randomUUID().toString() else parts[0]
                Habito(id, parts[1], parts[2], parts[3], parts[4].toBoolean())
            } else if (parts.size >= 4) {
                // Formato antiguo sin ID - generar uno nuevo
                Habito(java.util.UUID.randomUUID().toString(), parts[0], parts[1], parts[2], parts[3].toBoolean())
            } else if (parts.size >= 3) {
                // Formato muy antiguo - generar ID y usar valores por defecto
                Habito(java.util.UUID.randomUUID().toString(), parts[0], parts[1], parts[2], false)
            } else {
                // Si hay menos de 3 partes, crear hábito vacío con ID único
                Habito(java.util.UUID.randomUUID().toString(), "", "", "", false)
            }
        } catch (e: Exception) {
            // Si hay error en deserialización, retornar hábito vacío con ID único
            Habito(java.util.UUID.randomUUID().toString(), "", "", "", false)
        }
    }
    
    /**
     * Verifica si debe sincronizar con Firebase
     */
    private fun shouldSyncWithFirebase(): Boolean {
        val ctx = context ?: return false
        val settingsVM = _settingsViewModel.value ?: return false
        
        // Verificar conexión y configuración del usuario
        return NetworkUtils.isInternetAvailable(ctx) && settingsVM.shouldUseFirebase()
    }
    
    /**
     * Observa cambios en la conexión para sincronización automática
     */
    private fun observeConnectionChanges() {
        val ctx = context ?: return
        val settingsVM = _settingsViewModel.value ?: return
        val firebaseRepo = _firebaseRepository.value ?: return
        
        viewModelScope.launch {
            // Combinar observación de conexión y estado del modo
            combine(
                NetworkUtils.observeInternetConnection(ctx),
                settingsVM.isConnected,
                settingsVM.isOnlineModeEnabled
            ) { isConnected, _, onlineModeEnabled ->
                isConnected && onlineModeEnabled
            }.distinctUntilChanged().collect { shouldSync ->
                if (shouldSync && _pendingSyncHabits.value.isNotEmpty()) {
                    syncPendingHabits()
                }
            }
        }
    }
    
    /**
     * Sincroniza hábitos pendientes con Firebase
     */
    private suspend fun syncPendingHabits() {
        val ctx = context ?: return
        val firebaseRepo = _firebaseRepository.value ?: return
        
        try {
            val userId = "local_user" // TODO: Integrar con AuthViewModel
            val pendingHabits = _pendingSyncHabits.value.toList()
            
            for (habit in pendingHabits) {
                try {
                    val habitMap = mapOf(
                        "id" to habit.id,
                        "nombre" to habit.nombre,
                        "categoria" to habit.categoria,
                        "frecuencia" to habit.frecuencia,
                        "completado" to habit.completado,
                        "usuarioId" to userId
                    )
                    firebaseRepo.addHabit(userId, habitMap) { success, error ->
                        if (success) {
                            // Remover de la lista de pendientes
                            _pendingSyncHabits.value = _pendingSyncHabits.value.filter { it.id != habit.id }
                        }
                    }
                } catch (e: Exception) {
                    // Si falla un hábito específico, continuar con los demás
                    continue
                }
            }
            
            if (pendingHabits.isNotEmpty()) {
                showToast(ctx, "Hábitos locales sincronizados con la nube.")
            }
        } catch (e: Exception) {
            // Error general en la sincronización
            showToast(ctx, "Error al sincronizar hábitos.")
        }
    }
    
    /**
     * Agrega un hábito a la lista de sincronización pendiente
     */
    private fun addToPendingSync(habit: Habito) {
        _pendingSyncHabits.value = _pendingSyncHabits.value + habit
    }
    
    /**
     * Muestra un Toast en el hilo principal
     */
    private fun showToast(context: Context, message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Sincroniza manualmente todos los hábitos locales con Firebase
     */
    fun syncAllLocalHabits() {
        val ctx = context ?: return
        val firebaseRepo = _firebaseRepository.value ?: return
        
        if (!shouldSyncWithFirebase()) {
            showToast(ctx, "No hay conexión disponible para sincronizar.")
            return
        }
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = "local_user" // TODO: Integrar con AuthViewModel
                val localHabits = _habits.value
                
                for (habit in localHabits) {
                    try {
                        val habitMap = mapOf(
                            "id" to habit.id,
                            "nombre" to habit.nombre,
                            "categoria" to habit.categoria,
                            "frecuencia" to habit.frecuencia,
                            "completado" to habit.completado,
                            "usuarioId" to userId
                        )
                        firebaseRepo.addHabit(userId, habitMap) { success, error ->
                            // Continuar con los demás hábitos si uno falla
                        }
                    } catch (e: Exception) {
                        // Continuar con los demás hábitos si uno falla
                        continue
                    }
                }
                
                showToast(ctx, "Hábitos locales sincronizados con la nube.")
            } catch (e: Exception) {
                showToast(ctx, "Error al sincronizar hábitos.")
            }
        }
    }
}


