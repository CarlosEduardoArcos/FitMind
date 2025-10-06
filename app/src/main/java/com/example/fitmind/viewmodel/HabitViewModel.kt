package com.example.fitmind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitmind.core.AppConfig
import com.example.fitmind.data.mock.MockHabitRepository
import com.example.fitmind.data.model.Habito
import com.example.fitmind.data.model.Registro
import com.example.fitmind.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Habit CRUD and progress registration, exposing flows for UI.
 * Supports both Firebase and Mock modes.
 */
class HabitViewModel(
    private val firebaseRepository: HabitRepository = HabitRepository(),
    private val mockRepository: MockHabitRepository = MockHabitRepository()
) : ViewModel() {

    private val _habits = MutableStateFlow<List<Habito>>(emptyList())
    val habits: StateFlow<List<Habito>> = _habits.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun observeHabits(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (AppConfig.isMockMode) {
                    // En modo mock, usar el repositorio mock
                    mockRepository.getHabits(userId).collect { habitsList ->
                        _habits.value = habitsList
                    }
                } else {
                    // En modo Firebase, usar el repositorio real
                    firebaseRepository.obtenerHabitos(userId).collect { habitsList ->
                        _habits.value = habitsList
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al cargar hábitos"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createHabit(habito: Habito) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                if (AppConfig.isMockMode) {
                    mockRepository.saveHabit(habito)
                    // Actualizar la lista local
                    _habits.value = mockRepository.getHabitsList()
                } else {
                    firebaseRepository.crearHabito(habito)
                }
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al crear hábito"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addRecord(registro: Registro) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                if (AppConfig.isMockMode) {
                    // En modo mock, simular agregar registro
                    kotlinx.coroutines.delay(500)
                } else {
                    firebaseRepository.registrarProgreso(registro)
                }
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al agregar registro"
            } finally {
                _isLoading.value = false
            }
        }
    }
}


