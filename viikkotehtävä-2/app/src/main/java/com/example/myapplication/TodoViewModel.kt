package com.example.myapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class TodoViewModel : ViewModel() {

    // For the whole list of todos
    var todos by mutableStateOf<List<Todo>>(emptyList())
        private set

    // For the first todos
    var todo by mutableStateOf<Todo?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set


    fun loadTodo() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            delay(2000.milliseconds)
            try {
                todos = RetrofitInstance.api.getTodos()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Unknown error"
            } finally {
                isLoading = false
            }
        }
    }
}