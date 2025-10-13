package com.example.fitmind.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitmind.data.local.*
import com.example.fitmind.data.FirebaseRepository
import com.example.fitmind.model.Habito
import com.example.fitmind.utils.NetworkUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class HabitViewModel : ViewModel() {
    private var context: Context? = null
    private val _habits = MutableStateFlow<List<Habito>>(emptyList())
    val habits: StateFlow<List<Habito>> = _habits
    
    private val _settingsViewModel = MutableStateFlow<SettingsViewModel?>(null)
    private val _firebaseRepository = MutableStateFlow<FirebaseRepository?>(null)
    
    private val _pendingSyncHabits = MutableStateFlow<List<Habito>>(emptyList())
    val pendingSyncHabits: StateFlow<List<Habito>> = _pendingSyncHabits
    
    private val _pendingDeleteHabits = MutableStateFlow<List<String>>(emptyList())
    val pendingDeleteHabits: StateFlow<List<String>> = _pendingDeleteHabits

    fun initializeContext(context: Context, settingsViewModel: SettingsViewModel? = null, firebaseRepository: FirebaseRepository? = null) {
        this.context = context
        _settingsViewModel.value = settingsViewModel
        _firebaseRepository.value = firebaseRepository
        loadHabits()
        
        // Observar cambios en la conexión para sincronización automática
        observeConnectionChanges()
    }
    
    /**
     * Nueva función para agregar hábitos que maneja tanto Firebase como almacenamiento local
     */
    fun addHabit(habit: Habito) {
        val ctx = context ?: return
        val firebaseRepo = _firebaseRepository.value
        val settingsVM = _settingsViewModel.value
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Siempre guardar localmente primero
                saveHabitLocally(ctx, serializeHabito(habit))
                
                // Verificar si hay usuario autenticado
                val currentUser = FirebaseAuth.getInstance().currentUser
                val userId = currentUser?.uid
                
                if (userId != null && NetworkUtils.isInternetAvailable(ctx)) {
                    // Usuario autenticado y con conexión - guardar en Firebase
                    try {
                        val habitMap = mapOf(
                            "id" to habit.id,
                            "nombre" to habit.nombre,
                            "categoria" to habit.categoria,
                            "frecuencia" to habit.frecuencia,
                            "completado" to habit.completado,
                            "usuarioId" to userId
                        )
                        
                        firebaseRepo?.addHabit(userId, habitMap) { success, error ->
                            if (success) {
                                showToast(ctx, "✅ Hábito guardado en la nube y localmente")
                            } else {
                                // Si falla Firebase, mantener solo local
                                addToPendingSync(habit)
                                showToast(ctx, "⚠️ Guardado localmente. Se sincronizará cuando haya conexión.")
                            }
                        }
                    } catch (e: Exception) {
                        // Si hay error con Firebase, mantener solo local
                        addToPendingSync(habit)
                        showToast(ctx, "⚠️ Guardado localmente. Se sincronizará cuando haya conexión.")
                    }
                } else if (userId != null) {
                    // Usuario autenticado pero sin conexión
                    addToPendingSync(habit)
                    showToast(ctx, "⚠️ Sin conexión. Hábito guardado localmente.")
                } else {
                    // Usuario no autenticado (modo invitado)
                    showToast(ctx, "💾 Hábito guardado localmente (modo invitado)")
                }
            } catch (e: Exception) {
                showToast(ctx, "❌ Error al guardar el hábito")
            }
        }
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
        // Redirigir a la nueva función addHabit para mantener compatibilidad
        addHabit(hab)
    }

    fun deleteHabitLocal(hab: Habito) {
        val ctx = context ?: return
        val firebaseRepo = _firebaseRepository.value
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Siempre eliminar localmente primero
                deleteHabitLocally(ctx, serializeHabito(hab))
                
                // Verificar si hay usuario autenticado
                val currentUser = FirebaseAuth.getInstance().currentUser
                val userId = currentUser?.uid
                
                if (userId != null) {
                    if (NetworkUtils.isInternetAvailable(ctx) && firebaseRepo != null) {
                        // Usuario autenticado con conexión - eliminar de Firebase
                        try {
                            val success = firebaseRepo.deleteHabitFromFirebase(userId, hab.id)
                            if (success) {
                                showToast(ctx, "✅ Hábito eliminado de la nube y localmente")
                            } else {
                                // Si falla Firebase, agregar a pendientes de eliminación
                                addToPendingDelete(hab.id)
                                showToast(ctx, "⚠️ Eliminado localmente. Se sincronizará la eliminación.")
                            }
                        } catch (e: Exception) {
                            // Si hay error con Firebase, agregar a pendientes de eliminación
                            addToPendingDelete(hab.id)
                            showToast(ctx, "⚠️ Eliminado localmente. Se sincronizará la eliminación.")
                        }
                    } else {
                        // Usuario autenticado pero sin conexión - agregar a pendientes
                        addToPendingDelete(hab.id)
                        showToast(ctx, "⚠️ Sin conexión. Eliminado localmente.")
                    }
                } else {
                    // Usuario no autenticado (modo invitado) - solo local
                    showToast(ctx, "🗑️ Hábito eliminado localmente (modo invitado)")
                }
            } catch (e: Exception) {
                showToast(ctx, "❌ Error al eliminar el hábito")
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
                val currentUser = FirebaseAuth.getInstance().currentUser
                val userId = currentUser?.uid
                
                if (userId != null && NetworkUtils.isInternetAvailable(ctx) && firebaseRepo != null) {
                    try {
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
                // Solo sincronizar si hay usuario autenticado
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (shouldSync && (_pendingSyncHabits.value.isNotEmpty() || _pendingDeleteHabits.value.isNotEmpty()) && currentUser != null) {
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
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId = currentUser?.uid
            
            if (userId == null) {
                // No hay usuario autenticado, no se puede sincronizar
                return
            }
            
            // Sincronizar hábitos pendientes de agregar
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
            
            // Sincronizar eliminaciones pendientes
            val pendingDeletes = _pendingDeleteHabits.value.toList()
            for (habitId in pendingDeletes) {
                try {
                    val success = firebaseRepo.deleteHabitFromFirebase(userId, habitId)
                    if (success) {
                        // Remover de la lista de eliminaciones pendientes
                        _pendingDeleteHabits.value = _pendingDeleteHabits.value.filter { it != habitId }
                    }
                } catch (e: Exception) {
                    // Si falla una eliminación específica, continuar con las demás
                    continue
                }
            }
            
            if (pendingHabits.isNotEmpty() || pendingDeletes.isNotEmpty()) {
                showToast(ctx, "🔄 Hábitos locales sincronizados con la nube.")
            }
        } catch (e: Exception) {
            // Error general en la sincronización
            showToast(ctx, "❌ Error al sincronizar hábitos.")
        }
    }
    
    /**
     * Agrega un hábito a la lista de sincronización pendiente
     */
    private fun addToPendingSync(habit: Habito) {
        _pendingSyncHabits.value = _pendingSyncHabits.value + habit
    }
    
    /**
     * Agrega un ID de hábito a la lista de eliminación pendiente
     */
    private fun addToPendingDelete(habitId: String) {
        _pendingDeleteHabits.value = _pendingDeleteHabits.value + habitId
    }
    
    /**
     * Limpia hábitos obsoletos del almacenamiento local
     */
    suspend fun cleanObsoleteHabits(): Int {
        val ctx = context ?: return 0
        return try {
            val cleanedCount = cleanObsoleteHabits(ctx)
            if (cleanedCount > 0) {
                Log.d("HabitViewModel", "🧹 Limpieza completada: $cleanedCount hábitos antiguos eliminados")
                showToast(ctx, "🔄 Limpieza completada: $cleanedCount hábitos antiguos eliminados")
            }
            cleanedCount
        } catch (e: Exception) {
            Log.e("HabitViewModel", "Error al limpiar hábitos obsoletos", e)
            showToast(ctx, "❌ Error al limpiar hábitos antiguos")
            0
        }
    }
    
    /**
     * Sincroniza hábitos desde Firebase al almacenamiento local
     */
    suspend fun syncHabitsFromFirebase(): Boolean {
        val ctx = context ?: return false
        val firebaseRepo = _firebaseRepository.value ?: return false
        
        return try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId = currentUser?.uid
            
            if (userId == null || !NetworkUtils.isInternetAvailable(ctx)) {
                return false
            }
            
            Log.d("HabitViewModel", "🔄 Sincronizando hábitos desde Firebase para usuario $userId")
            
            // Obtener hábitos desde Firebase
            val firebaseHabits = firebaseRepo.getHabitsFromFirebase(userId)
            
            // Convertir a objetos Habito
            val firebaseHabitsList = firebaseHabits.mapNotNull { habitMap ->
                try {
                    Habito(
                        id = habitMap["id"]?.toString() ?: "",
                        nombre = habitMap["nombre"]?.toString() ?: "",
                        categoria = habitMap["categoria"]?.toString() ?: "",
                        frecuencia = habitMap["frecuencia"]?.toString() ?: "",
                        completado = habitMap["completado"] as? Boolean ?: false,
                        usuarioId = userId
                    )
                } catch (e: Exception) {
                    Log.w("HabitViewModel", "Error al convertir hábito desde Firebase", e)
                    null
                }
            }
            
            // Limpiar hábitos locales actuales
            clearAllHabits(ctx)
            
            // Guardar hábitos de Firebase localmente
            firebaseHabitsList.forEach { habit ->
                saveHabitLocally(ctx, serializeHabito(habit))
            }
            
            Log.d("HabitViewModel", "✅ Sincronización completada: ${firebaseHabitsList.size} hábitos sincronizados")
            showToast(ctx, "🔄 Hábitos sincronizados desde la nube")
            true
        } catch (e: Exception) {
            Log.e("HabitViewModel", "Error al sincronizar hábitos desde Firebase", e)
            showToast(ctx, "❌ Error al sincronizar hábitos")
            false
        }
    }
    
    /**
     * Ejecuta limpieza temporal automática (solo una vez)
     */
    suspend fun executeInitialCleanup(): Boolean {
        val ctx = context ?: return false
        
        return try {
            // Verificar si ya se ejecutó la limpieza
            val isCleanupDone = ctx.dataStore.data.map { prefs -> 
                prefs[FitMindKeys.CLEANUP_DONE] ?: false 
            }.first()
            
            if (isCleanupDone) {
                return false // Ya se ejecutó
            }
            
            Log.d("HabitViewModel", "🧹 Ejecutando limpieza inicial")
            
            // Ejecutar limpieza de hábitos obsoletos
            val cleanedCount = cleanObsoleteHabits(ctx)
            
            // Marcar como completado
            setCleanupDone(ctx)
            
            Log.d("HabitViewModel", "🧹 Limpieza inicial ejecutada: $cleanedCount hábitos antiguos eliminados")
            showToast(ctx, "🧹 Limpieza inicial ejecutada: $cleanedCount hábitos antiguos eliminados")
            true
        } catch (e: Exception) {
            Log.e("HabitViewModel", "Error en limpieza inicial", e)
            false
        }
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
        
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        
        if (userId == null) {
            showToast(ctx, "Debes estar autenticado para sincronizar hábitos.")
            return
        }
        
        if (!NetworkUtils.isInternetAvailable(ctx)) {
            showToast(ctx, "No hay conexión disponible para sincronizar.")
            return
        }
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
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



