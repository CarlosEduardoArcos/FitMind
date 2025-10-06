package com.example.fitmind.data.repository

import com.example.fitmind.data.model.Habito
import com.example.fitmind.data.model.Registro
import kotlinx.coroutines.flow.Flow

/**
 * Domain-specific repository that provides a clear API for Habits and Records,
 * delegating persistence to FirebaseRepository.
 */
class HabitRepository(
    private val firebaseRepository: FirebaseRepository = FirebaseRepository()
) {
    suspend fun crearHabito(habito: Habito) = firebaseRepository.saveHabit(habito)

    fun obtenerHabitos(userId: String): Flow<List<Habito>> = firebaseRepository.getHabits(userId)

    suspend fun registrarProgreso(registro: Registro) = firebaseRepository.addRecord(registro)

    fun obtenerRegistros(userId: String): Flow<List<Registro>> = firebaseRepository.getRecords(userId)
}


