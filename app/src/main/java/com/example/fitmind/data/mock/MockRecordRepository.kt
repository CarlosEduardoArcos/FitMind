package com.example.fitmind.data.mock

import com.example.fitmind.data.model.Registro
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repositorio mock para registros de hábitos.
 * Simula datos para gráficos y estadísticas.
 */
class MockRecordRepository {
    private val _records = MutableStateFlow<List<Registro>>(emptyList())
    val records: Flow<List<Registro>> = _records.asStateFlow()
    
    private val recordsList = mutableListOf<Registro>()

    init {
        // Cargar datos de ejemplo para gráficos
        loadSampleRecords()
    }

    suspend fun addRecord(registro: Registro) {
        kotlinx.coroutines.delay(300)
        
        val recordToSave = if (registro.id.isBlank()) {
            registro.copy(id = "record_${System.currentTimeMillis()}")
        } else {
            registro
        }
        
        recordsList.add(recordToSave)
        _records.value = recordsList.toList()
    }

    fun getRecords(userId: String): Flow<List<Registro>> {
        // En modo mock, devolvemos todos los registros
        return records
    }

    private fun loadSampleRecords() {
        val sampleRecords = mutableListOf<Registro>()
        
        // Generar registros de ejemplo para los últimos 30 días
        val habitIds = listOf("habit_1", "habit_2", "habit_3", "habit_4", "habit_5")
        
        for (day in 0..29) {
            val date = java.time.LocalDate.now().minusDays(day.toLong())
            
            habitIds.forEachIndexed { index, habitId ->
                val isCompleted = (day + index) % 3 != 0 // 66% completion rate
                sampleRecords.add(
                    Registro(
                        id = "record_${day}_${index}",
                        habitoId = habitId,
                        fecha = date.toString(),
                        completado = isCompleted
                    )
                )
            }
        }
        
        recordsList.addAll(sampleRecords)
        _records.value = recordsList.toList()
    }
}
