package com.example.fitmind.data.repository

import android.content.Context
import com.example.fitmind.data.local.saveHabitLocally
import com.example.fitmind.data.local.deleteHabitLocally
import com.example.fitmind.data.local.getLocalHabitsFlow
import com.example.fitmind.data.model.Habito
import com.example.fitmind.data.model.Registro
import kotlinx.coroutines.flow.Flow

/**
 * Domain-specific repository that provides a clear API for Habits and Records,
 * supporting both Firebase and local DataStore persistence.
 */
class HabitRepository(
    private val firebaseRepository: FirebaseRepository = FirebaseRepository()
) {
    suspend fun crearHabito(habito: Habito) = firebaseRepository.saveHabit(habito)

    fun obtenerHabitos(userId: String): Flow<List<Habito>> = firebaseRepository.getHabits(userId)

    suspend fun registrarProgreso(registro: Registro) = firebaseRepository.addRecord(registro)

    fun obtenerRegistros(userId: String): Flow<List<Registro>> = firebaseRepository.getRecords(userId)

    // Funciones para persistencia local con DataStore
    suspend fun saveHabitLocal(context: Context, habit: Habito) {
        saveHabitLocally(context, habit)
    }

    fun observeLocalHabits(context: Context): Flow<List<Habito>> {
        return getLocalHabitsFlow(context)
    }

    suspend fun deleteHabitLocal(context: Context, habit: Habito) {
        deleteHabitLocally(context, habit)
    }
}


