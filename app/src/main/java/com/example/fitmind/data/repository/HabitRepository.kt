package com.example.fitmind.data.repository

import android.content.Context
import com.example.fitmind.data.local.saveHabitLocally
import com.example.fitmind.data.local.deleteHabitLocally
import com.example.fitmind.data.local.getLocalHabitsFlow
import com.example.fitmind.model.Habito
import com.example.fitmind.data.model.Registro
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
        val serialized = "${habit.id}|${habit.nombre}|${habit.categoria}|${habit.frecuencia}|${habit.completado}"
        saveHabitLocally(context, serialized)
    }

    fun observeLocalHabits(context: Context): Flow<List<Habito>> {
        return getLocalHabitsFlow(context).map { set ->
            set.map { s ->
                val parts = s.split("|")
                if (parts.size >= 5) {
                    Habito(parts[0], parts[1], parts[2], parts[3], parts[4].toBoolean())
                } else if (parts.size >= 4) {
                    Habito("", parts[0], parts[1], parts[2], parts[3].toBoolean())
                } else {
                    Habito("", parts[0], parts[1], parts[2], false)
                }
            }
        }
    }

    suspend fun deleteHabitLocal(context: Context, habit: Habito) {
        val serialized = "${habit.id}|${habit.nombre}|${habit.categoria}|${habit.frecuencia}|${habit.completado}"
        deleteHabitLocally(context, serialized)
    }
}


