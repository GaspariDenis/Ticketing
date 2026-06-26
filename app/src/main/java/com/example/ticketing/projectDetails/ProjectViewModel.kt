package com.example.ticketing.projectDetails

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketing.network.APIStatus
import com.example.ticketing.repository.ProjectRepository
import com.example.ticketing.vo.Member
import com.example.ticketing.vo.Project
import com.example.ticketing.vo.UserTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(val repo : ProjectRepository) : ViewModel() {

    private val tag = "ProjectViewModel"
    val errorEvent = MutableStateFlow("")

    val project = MutableStateFlow(Project())

    fun getProject(projectId : String) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = repo.detailProject(projectId)

            when(status){
                is APIStatus.Success -> project.update { status.data }
                is APIStatus.Loading -> {}
                is APIStatus.ErrorAPI -> errorEvent.update { status.errorMessage() }
                is APIStatus.Error -> errorEvent.update { status.e.message ?: "Unexpected Error" }
            }
        }
    }

    fun removeMember(projectId : String, memberId : String){
        viewModelScope.launch(Dispatchers.IO) {
            val status = repo.removeMemberFromProject(projectId, memberId)

            when(status) {
                is APIStatus.Success -> {
                    getProject(projectId)
                }
                is APIStatus.Loading -> {}
                is APIStatus.ErrorAPI -> errorEvent.update { status.errorMessage() }
                is APIStatus.Error -> errorEvent.update { status.e.message ?: "Unexpected Error" }
            }
        }
    }

    fun addMember(projectId: String, email: String, role: UserTag) {
        viewModelScope.launch(Dispatchers.IO) {
            if(checkEmail(email)){
                val status = repo.addMemberFromProject(projectId, email, role)

                when(status) {
                    is APIStatus.Success -> {
                        getProject(projectId)
                    }
                    is APIStatus.Loading -> {}
                    is APIStatus.ErrorAPI -> errorEvent.update { status.errorMessage() }
                    is APIStatus.Error -> errorEvent.update { status.e.message ?: "Unexpected Error" }
                }
            }
        }
    }

    fun canRemoveOwner(list : List<Member>) : Boolean {
        var count = 0
        for(item in list){
            if(item.getRole() == UserTag.Owner)
                count++
        }
        return count > 1
    }

    fun checkEmail(email: String) : Boolean {
        if(TextUtils.isEmpty(email)){
            return false
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            errorEvent.update { "Inserisci una mail valida." }
            return false
        }

        return true
    }

    fun resetErrorEvent() {
        errorEvent.update { "" }
    }
}