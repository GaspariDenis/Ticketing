package com.example.ticketing.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketing.network.APIStatus
import com.example.ticketing.repository.AuthRepository
import com.example.ticketing.repository.ProjectRepository
import com.example.ticketing.vo.Member
import com.example.ticketing.vo.Project
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.annotation.meta.When
import javax.inject.Inject

@HiltViewModel
class dashboardViewModel @Inject constructor (
    val repoProject : ProjectRepository,
    val repoAuth : AuthRepository
) : ViewModel()  {

    private val tag = "dashboardViewModel"

    val errorEvent = MutableStateFlow("")

    val projects = MutableStateFlow(listOf<Project>())

    val isLogged = MutableStateFlow(true)

    fun resetErrorEvent() {
        errorEvent.update { "" }
    }

    fun getAllProject () {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repoProject.getAllProjects()
            var prj = listOf<Project>()

            when(result){
                is APIStatus.Success -> prj = result.data
                is APIStatus.ErrorAPI -> errorEvent.update { result.errorMessage() }
                is APIStatus.Error -> errorEvent.update { result.e.message ?: "" }
                else -> {}
            }

            var i = 0
            while(i < prj.lastIndex) {
                prj[i].role = (repoProject.getRoleForProject(prj[i].id ?: "") as APIStatus.Success).data.getRole()
                i++
            }

            projects.update { prj }
        }
    }

    fun createProject(name : String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repoProject.createProject(name, description)

            when(result){
                is APIStatus.Success -> { getAllProject() }
                is APIStatus.Loading -> {}
                is APIStatus.ErrorAPI -> errorEvent.update { result.errorMessage() }
                is APIStatus.Error -> errorEvent.update { result.e.message ?: "" }
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val status = repoAuth.logoutAccount()

            when(status) {
                is APIStatus.Success -> { isLogged.update { false } }
                is APIStatus.Loading -> {}
                is APIStatus.ErrorAPI -> errorEvent.update { status.errorMessage() }
                is APIStatus.Error -> errorEvent.update { status.e.message ?: "" }
            }
        }
    }

    private suspend fun getRole(projectId: String) : Member {
        val status = repoProject.getRoleForProject(projectId)
        when(status) {
            is APIStatus.Success ->  return status.data
            else -> {}
        }
        return Member(null, null, null, null)
    }
}