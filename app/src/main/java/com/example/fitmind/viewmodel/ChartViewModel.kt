package com.example.fitmind.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChartViewModel : ViewModel() {
    private val _chartData = MutableStateFlow<List<Any>>(emptyList())
    val chartData: StateFlow<List<Any>> = _chartData.asStateFlow()
}