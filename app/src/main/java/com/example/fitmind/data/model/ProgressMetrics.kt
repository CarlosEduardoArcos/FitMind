package com.example.fitmind.data.model

/**
 * Modelo para las métricas de progreso del usuario
 * Contiene todas las estadísticas calculadas dinámicamente
 */
data class ProgressMetrics(
    val totalHabits: Int = 0,
    val completedHabits: Int = 0,
    val completionPercentage: Float = 0f,
    val steps: Int = 0,
    val maxSteps: Int = 8000,
    val calories: Int = 0,
    val maxCalories: Int = 250,
    val kilometers: Float = 0f,
    val maxKilometers: Float = 5f,
    val heartRate: Int = 0,
    val averageHeartRate: Int = 70 // Frecuencia cardíaca promedio estimada
) {
    val stepsProgress: Float
        get() = if (maxSteps > 0) steps.toFloat() / maxSteps.toFloat() else 0f
    
    val caloriesProgress: Float
        get() = if (maxCalories > 0) calories.toFloat() / maxCalories.toFloat() else 0f
    
    val kilometersProgress: Float
        get() = if (maxKilometers > 0) kilometers / maxKilometers else 0f
    
    val heartRateProgress: Float
        get() = if (averageHeartRate > 0) heartRate.toFloat() / averageHeartRate.toFloat() else 0f
}
