package com.example.fitmind.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitmind.data.local.getLocalHabitsFlow
import com.example.fitmind.data.model.ProgressMetrics
import com.example.fitmind.model.Habito
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * ViewModel para gestionar las métricas de progreso del usuario
 * Calcula estadísticas dinámicas basadas en los hábitos registrados
 */
class ProgressViewModel(private val app: Application) : AndroidViewModel(app) {
    
    private val _progressMetrics = MutableStateFlow(ProgressMetrics())
    val progressMetrics: StateFlow<ProgressMetrics> = _progressMetrics.asStateFlow()
    
    private val _hasData = MutableStateFlow(false)
    val hasData: StateFlow<Boolean> = _hasData.asStateFlow()
    
    init {
        observeHabitsAndCalculateMetrics()
    }
    
    /**
     * Observa los hábitos locales y calcula las métricas automáticamente
     */
    private fun observeHabitsAndCalculateMetrics() {
        viewModelScope.launch {
            try {
                getLocalHabitsFlow(app.applicationContext)
                    .collect { habitsSet ->
                        val habitsList = habitsSet.map { deserializeHabito(it) }
                        calculateMetrics(habitsList)
                    }
            } catch (e: Exception) {
                // Si hay error, inicializar con métricas vacías
                _progressMetrics.value = ProgressMetrics(
                    totalHabits = 0,
                    completedHabits = 0,
                    completionPercentage = 0f,
                    steps = 0,
                    calories = 0,
                    kilometers = 0f, // OPT: Cambiar de Double a Float
                    heartRate = 0
                )
                _hasData.value = false
            }
        }
    }
    
    /**
     * Calcula las métricas de progreso basadas en los hábitos del usuario
     */
    private fun calculateMetrics(habits: List<Habito>) {
        val totalHabits = habits.size
        val completedHabits = habits.count { it.completado }
        val completionPercentage = if (totalHabits > 0) {
            (completedHabits.toFloat() / totalHabits.toFloat()) * 100f
        } else 0f
        
        // Calcular métricas de fitness basadas en hábitos completados
        val steps = calculateStepsFromHabits(habits)
        val calories = calculateCaloriesFromHabits(habits)
        val kilometers = calculateKilometersFromHabits(habits)
        val heartRate = calculateHeartRateFromHabits(habits)
        
        val metrics = ProgressMetrics(
            totalHabits = totalHabits,
            completedHabits = completedHabits,
            completionPercentage = completionPercentage,
            steps = steps,
            calories = calories,
            kilometers = kilometers,
            heartRate = heartRate
        )
        
        _progressMetrics.value = metrics
        _hasData.value = totalHabits > 0
    }
    
    /**
     * Calcula pasos basados en hábitos de ejercicio completados
     */
    private fun calculateStepsFromHabits(habits: List<Habito>): Int {
        val exerciseHabits = habits.filter { 
            it.completado && it.categoria.lowercase().contains("ejercicio") ||
            it.categoria.lowercase().contains("deporte") ||
            it.categoria.lowercase().contains("fitness")
        }
        
        // Base de pasos diarios + bonus por hábitos de ejercicio
        val baseSteps = 2000
        val exerciseBonus = exerciseHabits.size * 1500
        
        return minOf(baseSteps + exerciseBonus, 8000)
    }
    
    /**
     * Calcula calorías quemadas basadas en hábitos completados
     */
    private fun calculateCaloriesFromHabits(habits: List<Habito>): Int {
        val activeHabits = habits.filter { it.completado }
        
        // Base de calorías + bonus por hábitos activos
        val baseCalories = 50
        val activeBonus = activeHabits.size * 30
        
        return minOf(baseCalories + activeBonus, 250)
    }
    
    /**
     * Calcula kilómetros recorridos basados en hábitos de ejercicio
     */
    private fun calculateKilometersFromHabits(habits: List<Habito>): Float {
        val cardioHabits = habits.filter { 
            it.completado && (
                it.nombre.lowercase().contains("correr") ||
                it.nombre.lowercase().contains("caminar") ||
                it.nombre.lowercase().contains("ciclismo") ||
                it.categoria.lowercase().contains("cardio")
            )
        }
        
        val baseKm = 0.5f
        val cardioBonus = cardioHabits.size * 0.8f
        
        return minOf(baseKm + cardioBonus, 5f)
    }
    
    /**
     * Calcula frecuencia cardíaca promedio basada en actividad
     */
    private fun calculateHeartRateFromHabits(habits: List<Habito>): Int {
        val activeHabits = habits.filter { it.completado }
        
        // Frecuencia cardíaca base en reposo + incremento por actividad
        val baseHeartRate = 70
        val activityIncrease = activeHabits.size * 5
        
        return minOf(baseHeartRate + activityIncrease, 120)
    }
    
    /**
     * Deserializa un hábito desde string
     */
    private fun deserializeHabito(s: String): Habito {
        val parts = s.split("|")
        return if (parts.size >= 5) {
            Habito(parts[0], parts[1], parts[2], parts[3], parts[4].toBoolean())
        } else if (parts.size >= 4) {
            Habito("", parts[0], parts[1], parts[2], parts[3].toBoolean())
        } else {
            Habito("", parts[0], parts[1], parts[2], false)
        }
    }
    
    /**
     * Refresca las métricas manualmente
     */
    fun refreshMetrics() {
        viewModelScope.launch {
            // La observación automática ya maneja las actualizaciones
            // Este método se puede usar para forzar recálculo si es necesario
        }
    }
}
