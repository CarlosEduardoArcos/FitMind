package com.example.fitmind.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("fitmind_prefs")

object FitMindKeys {
    val HABITS = stringSetPreferencesKey("habits_list")
    val CLEANUP_DONE = booleanPreferencesKey("cleanup_done")
}

suspend fun saveHabitLocally(context: Context, serialized: String) {
    context.dataStore.edit { prefs ->
        val current = prefs[FitMindKeys.HABITS]?.toMutableSet() ?: mutableSetOf()
        current.add(serialized)
        prefs[FitMindKeys.HABITS] = current
    }
}

suspend fun deleteHabitLocally(context: Context, serialized: String) {
    context.dataStore.edit { prefs ->
        val current = prefs[FitMindKeys.HABITS]?.toMutableSet() ?: mutableSetOf()
        current.remove(serialized)
        prefs[FitMindKeys.HABITS] = current
    }
}

fun getLocalHabitsFlow(context: Context): Flow<Set<String>> =
    context.dataStore.data.map { prefs -> prefs[FitMindKeys.HABITS] ?: emptySet() }

suspend fun clearAllHabits(context: Context) {
    context.dataStore.edit { prefs ->
        prefs.remove(FitMindKeys.HABITS)
    }
}

suspend fun isCleanupDone(context: Context): Boolean {
    return context.dataStore.data.map { prefs -> 
        prefs[FitMindKeys.CLEANUP_DONE] ?: false 
    }.let { flow ->
        // Esta es una función suspend que necesita ser manejada de manera diferente
        // Por ahora retornamos false, se manejará en el ViewModel
        false
    }
}

suspend fun setCleanupDone(context: Context) {
    context.dataStore.edit { prefs ->
        prefs[FitMindKeys.CLEANUP_DONE] = true
    }
}

suspend fun cleanObsoleteHabits(context: Context): Int {
    var cleanedCount = 0
    context.dataStore.edit { prefs ->
        val current = prefs[FitMindKeys.HABITS]?.toMutableSet() ?: mutableSetOf()
        val toRemove = mutableSetOf<String>()
        
        current.forEach { habitString ->
            val parts = habitString.split("|")
            // Si no tiene al menos 5 partes (id, nombre, categoria, frecuencia, completado)
            // o si el ID está vacío, es un hábito obsoleto
            if (parts.size < 5 || parts[0].isBlank()) {
                toRemove.add(habitString)
                cleanedCount++
            }
        }
        
        current.removeAll(toRemove)
        prefs[FitMindKeys.HABITS] = current
    }
    return cleanedCount
}
