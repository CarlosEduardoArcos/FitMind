package com.example.fitmind.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.fitmind.data.model.Habito
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DataStoreManager(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("fitmind_prefs", Context.MODE_PRIVATE)
    private val _habits = MutableStateFlow<List<Habito>>(emptyList())
    val habits: Flow<List<Habito>> = _habits.asStateFlow()
    
    init {
        loadHabitsFromPrefs()
    }
    
    private fun loadHabitsFromPrefs() {
        val habitsSet = prefs.getStringSet("habits_list", emptySet()) ?: emptySet()
        val habitsList = habitsSet.map { habitString ->
            val parts = habitString.split("|")
            if (parts.size >= 5) {
                Habito(
                    nombre = parts[0],
                    categoria = parts[1],
                    frecuencia = parts[2],
                    fechaInicio = parts[3],
                    usuarioId = parts[4]
                )
            } else {
                // Handle old format or corrupted data
                Habito(
                    nombre = parts.getOrNull(0) ?: "",
                    categoria = parts.getOrNull(1) ?: "",
                    frecuencia = parts.getOrNull(2) ?: "",
                    fechaInicio = System.currentTimeMillis().toString(),
                    usuarioId = "local_user"
                )
            }
        }
        _habits.value = habitsList
    }
    
    suspend fun saveHabitLocally(habit: Habito) {
        val current = prefs.getStringSet("habits_list", emptySet())?.toMutableSet() ?: mutableSetOf()
        val habitString = "${habit.nombre}|${habit.categoria}|${habit.frecuencia}|${habit.fechaInicio}|${habit.usuarioId}"
        current.add(habitString)
        prefs.edit().putStringSet("habits_list", current).apply()
        loadHabitsFromPrefs()
    }
    
    fun getLocalHabits(): Flow<List<Habito>> = habits
    
    suspend fun clearAllHabits() {
        prefs.edit().remove("habits_list").apply()
        _habits.value = emptyList()
    }
    
    suspend fun removeHabit(habit: Habito) {
        val current = prefs.getStringSet("habits_list", emptySet())?.toMutableSet() ?: mutableSetOf()
        val habitString = "${habit.nombre}|${habit.categoria}|${habit.frecuencia}|${habit.fechaInicio}|${habit.usuarioId}"
        current.remove(habitString)
        prefs.edit().putStringSet("habits_list", current).apply()
        loadHabitsFromPrefs()
    }
}
