package com.example.fitmind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitmind.core.AppConfig
import com.example.fitmind.data.mock.MockRecordRepository
import com.example.fitmind.data.model.Registro
import com.example.fitmind.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * ViewModel that prepares data from records for charting (weekly and monthly).
 * Output format is simplified for easy integration with MPAndroidChart later.
 * Supports both Firebase and Mock modes.
 */
class ChartViewModel(
    private val firebaseRepository: HabitRepository = HabitRepository(),
    private val mockRepository: MockRecordRepository = MockRecordRepository()
) : ViewModel() {

    data class ChartEntry(val x: Float, val y: Float)

    private val _weeklyData = MutableStateFlow<List<ChartEntry>>(emptyList())
    val weeklyData: StateFlow<List<ChartEntry>> = _weeklyData.asStateFlow()

    private val _monthlyData = MutableStateFlow<List<ChartEntry>>(emptyList())
    val monthlyData: StateFlow<List<ChartEntry>> = _monthlyData.asStateFlow()

    fun observeWeekly(userId: String) {
        viewModelScope.launch {
            if (AppConfig.isMockMode) {
                mockRepository.getRecords(userId).map { registros ->
                    aggregateByWeek(registros)
                }.collect { _weeklyData.value = it }
            } else {
                firebaseRepository.obtenerRegistros(userId).map { registros ->
                    aggregateByWeek(registros)
                }.collect { _weeklyData.value = it }
            }
        }
    }

    fun observeMonthly(userId: String) {
        viewModelScope.launch {
            if (AppConfig.isMockMode) {
                mockRepository.getRecords(userId).map { registros ->
                    aggregateByMonth(registros)
                }.collect { _monthlyData.value = it }
            } else {
                firebaseRepository.obtenerRegistros(userId).map { registros ->
                    aggregateByMonth(registros)
                }.collect { _monthlyData.value = it }
            }
        }
    }

    private fun aggregateByWeek(registros: List<Registro>): List<ChartEntry> {
        // Placeholder aggregation: count completed per day index 0..6
        val counts = IntArray(7)
        registros.filter { it.completado }.forEachIndexed { index, _ ->
            counts[index % 7]++
        }
        return counts.mapIndexed { i, v -> ChartEntry(i.toFloat(), v.toFloat()) }
    }

    private fun aggregateByMonth(registros: List<Registro>): List<ChartEntry> {
        // Placeholder aggregation: count completed per day index 0..29
        val counts = IntArray(30)
        registros.filter { it.completado }.forEachIndexed { index, _ ->
            counts[index % 30]++
        }
        return counts.mapIndexed { i, v -> ChartEntry(i.toFloat(), v.toFloat()) }
    }
}


