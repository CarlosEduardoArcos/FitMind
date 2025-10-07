package com.example.fitmind.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("fitmind_prefs")

object FitMindKeys {
    val HABITS = stringSetPreferencesKey("habits_list")
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
