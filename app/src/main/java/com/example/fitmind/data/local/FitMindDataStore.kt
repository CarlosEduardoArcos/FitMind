package com.example.fitmind.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.fitmind.data.model.Habito
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

val Context.dataStore by preferencesDataStore("fitmind_prefs")

object FitMindKeys {
    val HABITS = stringSetPreferencesKey("habits_list")
}

// Funciones de serialización
fun serializeHabito(habito: Habito): String {
    return "${habito.id}|${habito.nombre}|${habito.categoria}|${habito.frecuencia}|${habito.fechaInicio}|${habito.usuarioId}"
}

fun deserializeHabito(serialized: String): Habito {
    val parts = serialized.split("|")
    return if (parts.size >= 6) {
        Habito(
            id = parts[0],
            nombre = parts[1],
            categoria = parts[2],
            frecuencia = parts[3],
            fechaInicio = parts[4],
            usuarioId = parts[5]
        )
    } else {
        Habito() // Retornar hábito vacío si hay error en deserialización
    }
}

suspend fun saveHabitLocally(context: Context, habito: Habito) {
    val habitoWithId = if (habito.id.isEmpty()) {
        habito.copy(id = UUID.randomUUID().toString())
    } else {
        habito
    }
    val serialized = serializeHabito(habitoWithId)
    context.dataStore.edit { prefs ->
        val current = prefs[FitMindKeys.HABITS]?.toMutableSet() ?: mutableSetOf()
        current.add(serialized)
        prefs[FitMindKeys.HABITS] = current
    }
}

suspend fun deleteHabitLocally(context: Context, habito: Habito) {
    val serialized = serializeHabito(habito)
    context.dataStore.edit { prefs ->
        val current = prefs[FitMindKeys.HABITS]?.toMutableSet() ?: mutableSetOf()
        current.remove(serialized)
        prefs[FitMindKeys.HABITS] = current
    }
}

fun getLocalHabitsFlow(context: Context): Flow<List<Habito>> =
    context.dataStore.data.map { prefs -> 
        (prefs[FitMindKeys.HABITS] ?: emptySet()).map { serialized ->
            deserializeHabito(serialized)
        }
    }
