package com.punyo.moodlefunctioncaller.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainScreenViewModel(private val moodleRepository: MoodleRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(MainScreenUiState())
    val uiState: StateFlow<MainScreenUiState> = _uiState.asStateFlow()

    suspend fun getUserInfo(): UserInfo {
        return moodleRepository.getUserInfo()
    }

    suspend fun getUserCourses(): List<Course> {
        return moodleRepository.getUserCourses()
    }

    fun setOutput(output: String) {
        _uiState.value = _uiState.value.copy(currentOutput = output)
    }

    fun setArgs1(args1: String) {
        _uiState.value = _uiState.value.copy(args1 = args1)
    }

    fun setArgs2(args2: String) {
        _uiState.value = _uiState.value.copy(args2 = args2)
    }

    class Factory(
        private val moodleRepository: MoodleRepository
    ) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainScreenViewModel(moodleRepository) as T
        }
    }
}

data class MainScreenUiState(
    val currentOutput: String = "",
    val args1: String = "",
    val args2: String = "",
)