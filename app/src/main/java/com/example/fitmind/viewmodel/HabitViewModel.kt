package com.example.fitmind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitmind.data.model.Habito
import com.example.fitmind.data.model.Registro
import com.example.fitmind.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Habit CRUD and progress registration, exposing flows for UI.
 */
class HabitViewModel(
    private val repository: HabitRepository = HabitRepository()
) : ViewModel() {

    private val _habits = MutableStateFlow<List<Habito>>(emptyList())
    val habits: StateFlow<List<Habito>> = _habits.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun observeHabits(userId: String) {
        // Mock: simulate loading habits
        viewModelScope.launch {
            _isLoading.value = true
            kotlinx.coroutines.delay(500) // Simulate network delay
            _isLoading.value = false
            // Mock: return some sample habits
            _habits.value = listOf(
                Habito(
                    id = "1",
                    nombre = "Ejercicio diario",
                    categoria = "Salud",
                    frecuencia = "Diario",
                    fechaInicio = System.currentTimeMillis().toString(),
                    usuarioId = userId
                ),
                Habito(
                    id = "2", 
                    nombre = "Leer 30 minutos",
                    categoria = "Educación",
                    frecuencia = "Diario",
                    fechaInicio = System.currentTimeMillis().toString(),
                    usuarioId = userId
                )
            )
        }
    }

    fun createHabit(habito: Habito) {
        _isLoading.value = true
        viewModelScope.launch {
            // Mock: simulate network delay
            kotlinx.coroutines.delay(1000)
            _isLoading.value = false
            
            // Mock: add to local list
            val newHabit = habito.copy(id = System.currentTimeMillis().toString())
            _habits.value = _habits.value + newHabit
            _errorMessage.value = null
        }
    }

    fun addRecord(registro: Registro) {
        _isLoading.value = true
        viewModelScope.launch {
            // Mock: simulate network delay
            kotlinx.coroutines.delay(500)
            _isLoading.value = false
            _errorMessage.value = null
        }
    }
}


