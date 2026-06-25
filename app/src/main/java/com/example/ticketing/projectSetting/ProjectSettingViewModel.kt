package com.example.ticketing.projectSetting

import android.accessibilityservice.GestureDescription
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ticketing.network.APIStatus
import com.example.ticketing.repository.ProjectRepository
import com.example.ticketing.vo.Project
import com.example.ticketing.vo.UIProject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectSettingViewModel @Inject constructor(
    val repo : ProjectRepository
) : ViewModel() {
    private val tag = "ProjectSettingViewModel"

    val successfulOperation = MutableStateFlow(false)

    val errorEvent = MutableStateFlow("")

    fun resetError() {
        errorEvent.update { "" }
    }

    fun uploaProject(projectId : String, title : String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = repo.updateProject(UIProject(
                id = projectId,
                name = title,
                description = description,
                members = listOf()
            ))

            when(status) {
                is APIStatus.ErrorAPI -> { errorEvent.update { status.errorMessage() } }
                is APIStatus.Error -> { errorEvent.update { status.e.message ?: "Unexpected error" } }
                is APIStatus.Loading -> {}
                is APIStatus.Success -> { successfulOperation.update { true } }
            }
        }
    }

    fun deleteProject(projectId: String){
        viewModelScope.launch(Dispatchers.IO) {
            val status = repo.deleteProject(UIProject(
                id = projectId,
                name = "",
                description = "",
                members = listOf()
            ))

            when(status) {
                is APIStatus.ErrorAPI -> { errorEvent.update { status.errorMessage() } }
                is APIStatus.Error -> { errorEvent.update { status.e.message ?: "Unexpected error" } }
                is APIStatus.Loading -> {}
                is APIStatus.Success -> { successfulOperation.update { true } }
            }
        }
    }
}