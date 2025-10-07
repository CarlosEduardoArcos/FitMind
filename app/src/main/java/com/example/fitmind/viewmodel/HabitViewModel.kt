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
        viewModelScope.launch {
            getLocalHabitsFlow(app.applicationContext)
                .map { set -> set.map { s -> deserializeHabito(s) } }
                .collect { list -> _habits.value = list }
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

    private fun serializeHabito(h: Habito) =
        "${h.nombre}|${h.categoria}|${h.frecuencia}"

    private fun deserializeHabito(s: String): Habito {
        val parts = s.split("|")
        return Habito(parts[0], parts[1], parts[2])
    }
}


