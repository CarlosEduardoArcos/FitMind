package com.example.fitmind.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitmind.data.local.*
import com.example.fitmind.model.Habito
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HabitViewModel(private val app: Application) : AndroidViewModel(app) {
    private val _habits = MutableStateFlow<List<Habito>>(emptyList())
    val habits: StateFlow<List<Habito>> = _habits

    init {
        // OPT: Inicialización segura con manejo de errores
        viewModelScope.launch {
            try {
                getLocalHabitsFlow(app.applicationContext)
                    .map { set -> set.map { s -> deserializeHabito(s) } }
                    .collect { list -> _habits.value = list }
            } catch (e: Exception) {
                // Si hay error, inicializar con lista vacía
                _habits.value = emptyList()
            }
        }
    }

    fun addHabitLocal(hab: Habito) {
        viewModelScope.launch(Dispatchers.IO) {
            saveHabitLocally(app.applicationContext, serializeHabito(hab))
        }
    }

    fun deleteHabitLocal(hab: Habito) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteHabitLocally(app.applicationContext, serializeHabito(hab))
        }
    }

    fun toggleComplete(hab: Habito) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = hab.copy(completado = !hab.completado)
            deleteHabitLocally(app.applicationContext, serializeHabito(hab))
            saveHabitLocally(app.applicationContext, serializeHabito(updated))
        }
    }

    private fun serializeHabito(h: Habito) =
        "${h.id}|${h.nombre}|${h.categoria}|${h.frecuencia}|${h.completado}"

    private fun deserializeHabito(s: String): Habito {
        val parts = s.split("|")
        return if (parts.size >= 5) {
            Habito(parts[0], parts[1], parts[2], parts[3], parts[4].toBoolean())
        } else if (parts.size >= 4) {
            Habito("", parts[0], parts[1], parts[2], parts[3].toBoolean())
        } else {
            Habito("", parts[0], parts[1], parts[2], false)
        }
    }
}


