package com.example.fitmind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitmind.data.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    private val repo = FirebaseRepository()
    
    private val _users = MutableStateFlow<List<Pair<String, Map<String, Any>>>>(emptyList())
    val users: StateFlow<List<Pair<String, Map<String, Any>>>> = _users.asStateFlow()
    
    private val _selectedUserHabits = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val selectedUserHabits: StateFlow<List<Map<String, Any>>> = _selectedUserHabits.asStateFlow()
    
    private val _totalUsers = MutableStateFlow(0)
    val totalUsers: StateFlow<Int> = _totalUsers.asStateFlow()
    
    private val _totalHabits = MutableStateFlow(0)
    val totalHabits: StateFlow<Int> = _totalHabits.asStateFlow()
    
    private val _averageHabits = MutableStateFlow(0.0)
    val averageHabits: StateFlow<Double> = _averageHabits.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadUsers()
        loadStatistics()
    }

    fun loadUsers() {
        _isLoading.value = true
        repo.getAllUsers { usersList ->
            _users.value = usersList
            _isLoading.value = false
        }
    }

    fun loadHabitsForUser(userId: String) {
        repo.getUserHabits(userId) { habits ->
            _selectedUserHabits.value = habits
        }
    }

    fun loadStatistics() {
        viewModelScope.launch {
            repo.getUsersCount { userCount ->
                _totalUsers.value = userCount
                updateAverageHabits()
            }
            
            repo.getTotalHabitsCount { habitsCount ->
                _totalHabits.value = habitsCount
                updateAverageHabits()
            }
        }
    }

    private fun updateAverageHabits() {
        val users = _totalUsers.value
        val habits = _totalHabits.value
        
        if (users > 0) {
            _averageHabits.value = habits.toDouble() / users.toDouble()
        } else {
            _averageHabits.value = 0.0
        }
    }

    fun clearSelectedUserHabits() {
        _selectedUserHabits.value = emptyList()
    }
}
