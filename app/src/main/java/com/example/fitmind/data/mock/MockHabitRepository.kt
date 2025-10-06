package com.example.fitmind.data.mock

import com.example.fitmind.core.AppConfig
import com.example.fitmind.data.model.Habito
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repositorio mock para hábitos.
 * Simula el comportamiento de Firestore sin conexión real.
 */
class MockHabitRepository {
    private val _habits = MutableStateFlow<List<Habito>>(emptyList())
    val habits: Flow<List<Habito>> = _habits.asStateFlow()
    
    private val habitsList = mutableListOf<Habito>()

    init {
        // Cargar datos de ejemplo
        loadSampleHabits()
    }

    suspend fun saveHabit(habito: Habito) {
        // Simular delay de red
        kotlinx.coroutines.delay(500)
        
        val habitToSave = if (habito.id.isBlank()) {
            habito.copy(id = "habit_${System.currentTimeMillis()}")
        } else {
            habito
        }
        
        val existingIndex = habitsList.indexOfFirst { it.id == habitToSave.id }
        if (existingIndex >= 0) {
            habitsList[existingIndex] = habitToSave
        } else {
            habitsList.add(habitToSave)
        }
        
        _habits.value = habitsList.toList()
    }

    fun getHabits(userId: String): Flow<List<Habito>> {
        // En modo mock, devolvemos todos los hábitos
        return habits
    }

    suspend fun deleteHabit(habitId: String) {
        kotlinx.coroutines.delay(300)
        habitsList.removeAll { it.id == habitId }
        _habits.value = habitsList.toList()
    }

    fun getHabitsList(): List<Habito> = habitsList.toList()

    private fun loadSampleHabits() {
        val sampleHabits = listOf(
            Habito(
                id = "habit_1",
                nombre = "Meditar 10 minutos",
                categoria = "Bienestar",
                frecuencia = "Diario",
                fechaInicio = "2024-01-01",
                usuarioId = AppConfig.Mock.defaultUserId
            ),
            Habito(
                id = "habit_2", 
                nombre = "Leer 20 minutos",
                categoria = "Crecimiento personal",
                frecuencia = "Diario",
                fechaInicio = "2024-01-01",
                usuarioId = AppConfig.Mock.defaultUserId
            ),
            Habito(
                id = "habit_3",
                nombre = "Ejercicio físico",
                categoria = "Salud",
                frecuencia = "3 veces por semana",
                fechaInicio = "2024-01-01",
                usuarioId = AppConfig.Mock.defaultUserId
            ),
            Habito(
                id = "habit_4",
                nombre = "Beber 8 vasos de agua",
                categoria = "Salud",
                frecuencia = "Diario",
                fechaInicio = "2024-01-01",
                usuarioId = AppConfig.Mock.defaultUserId
            ),
            Habito(
                id = "habit_5",
                nombre = "Escribir en el diario",
                categoria = "Bienestar",
                frecuencia = "Diario",
                fechaInicio = "2024-01-01",
                usuarioId = AppConfig.Mock.defaultUserId
            )
        )
        
        habitsList.addAll(sampleHabits)
        _habits.value = habitsList.toList()
    }
}
