package com.example.ticketing.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketing.network.APIStatus
import com.example.ticketing.repository.ProjectRepository
import com.example.ticketing.vo.Member
import com.example.ticketing.vo.Project
import com.example.ticketing.vo.UserTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class dashboardViewModel @Inject constructor (
    val repo : ProjectRepository
) : ViewModel()  {

    private val tag = "dashboardViewModel"

    val errorEvent = MutableStateFlow("")

    val projects = MutableStateFlow(listOf<Project>())

    fun resetErrorEvent() {
        errorEvent.update { "" }
    }

    fun getAllProject () {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.getAllProjects()
            var prj = listOf<Project>()

            when(result){
                is APIStatus.Success -> prj = result.data
                is APIStatus.ErrorAPI -> errorEvent.update { result.errorMessage() }
                is APIStatus.Error -> errorEvent.update { result.e.message ?: "" }
                else -> {}
            }

            var i = 0
            while(i < prj.lastIndex) {
                prj[i].role = (repo.getRoleForProject(prj[i].id ?: "") as APIStatus.Success).data.getRole()
                i++
            }

            projects.update { prj }
        }
    }


    private suspend fun getRole(projectId: String) : Member {
        val status = repo.getRoleForProject(projectId)
        when(status) {
            is APIStatus.Success ->  return status.data
            else -> {}
        }
        return Member(null, null, null)
    }
}